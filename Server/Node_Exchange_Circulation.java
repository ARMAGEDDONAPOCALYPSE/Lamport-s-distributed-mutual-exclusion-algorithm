import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Connections between nodes with lock and condition
public class Node_Exchange_Circulation {
	static ServerSocket_Semaphore bound_socket;

	static HashMap<Integer, ObjectOutputStream_Semaphore> output_semaphore = new HashMap<Integer, ObjectOutputStream_Semaphore>();

	// Ready check for connections
	// Where a Lock replaces the use of synchronized methods and statements,
	// a Condition replaces the use of the Object monitor methods.
	public static ReentrantLock connection_lock = new ReentrantLock();
	public static Condition ready_connection_all = connection_lock.newCondition();

	public static void BindServerSocket(int port) {
		try {
			bound_socket = new ServerSocket_Semaphore(port);
		} catch (IOException e) {
			System.out.println("Cannot bind to specified server socket.");
			System.exit(-1);
		}
	}

	public static Socket connection_next() throws InterruptedException, IOException {
		try {
			ServerSocket active_server_socket = bound_socket.acquire_socket();
			Socket new_socket_connection = active_server_socket.accept();
			bound_socket.release_socket();
			return new_socket_connection;
		} finally {
			bound_socket.release_socket();
		}
	}

	public static void listening_to_active_nodes(ArrayList<Node_Attributes> grid) {
		for (@SuppressWarnings("unused")
		Node_Attributes next_node : grid) {
			Thread next_node_exchange = new Thread(new Node_Exchange(bound_socket));
			next_node_exchange.start();
		}
	}

	@SuppressWarnings("resource")
	public static void connecting_to_active_nodes(ArrayList<Node_Attributes> grid) {
		for (int i = 0; i < grid.size(); i++) {
			Node_Attributes next_node = grid.get(i);
			try {
				Socket client_socket = new Socket(next_node.getNodeIP(), next_node.getNodePort());
				ObjectOutputStream output = new ObjectOutputStream(client_socket.getOutputStream());
				new_output_stream(next_node, output);
			} catch (IOException e) {
				// System.out.println("Error connecting with one of the nodes.");
				// TODO: What to do here?
				i--;
			}
		}
	}

	public static synchronized void new_output_stream(Node_Attributes connected_node, ObjectOutputStream stream) {
		// if (Server.DEBUG) System.out.println("Adding stream for " +
		// connectedNode.getID() + " :: " + nodeStream);
		if (stream != null)
			output_semaphore.put(connected_node.getID(), new ObjectOutputStream_Semaphore(stream));
	}

	public static void waiting_for_active_connections_to_set(int node_numbers) {
		while (true) {
			connection_lock.lock();
			try {
				while (Node_Map.get_active_node() < (node_numbers))
					ready_connection_all.await();

				break;
			} catch (InterruptedException e) {
				continue;
			} finally {
				connection_lock.unlock();
			}
		}

	}

	public static void send_to_node(Mutual_Exclusion msg, int node) {
		ObjectOutputStream output;
		while (true) {
			try {
				ObjectOutputStream_Semaphore curr = output_semaphore.get(node);

				// If there is nothing, then can not be sent
				if (curr == null) {
					return;
				}

				output = curr.acquire_stream();
				break;
			} catch (InterruptedException e) {
				// continue to try, no need to curr.release_stream()
				continue;
			}
		}

		try {
			output.writeObject(msg);
		} catch (IOException e) {
			if (Server.DEBUG)
				System.out.println("Could not send message.");
			if (Server.DEBUG)
				System.out.println("Error: " + e + ", message: " + e.getMessage());

		} finally {
			output_semaphore.get(node).release_stream();
		}

	}

	public static void broadcast(Mutual_Exclusion msg) {
		ArrayList<Node_Attributes> all_nodes = Node_Map.get_all_nodes();

		for (Node_Attributes next_node : all_nodes) {
			send_to_node(msg, next_node.getID());
		}
	}

}
