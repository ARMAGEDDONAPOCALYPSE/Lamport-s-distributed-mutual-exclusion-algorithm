import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Node_Exchange implements Runnable {
	Node_Attributes active_node = null;
	ServerSocket_Semaphore bound_socket;

	public Node_Exchange(ServerSocket_Semaphore socket) {
		this.bound_socket = socket;
	}

	@Override
	public void run() {
		// Connection from peers
		Socket connected_socket;
		ObjectInputStream input = null;
		while (true) {
			try {
				connected_socket = Node_Exchange_Circulation.connection_next();

				if (Server.DEBUG)
					System.out.println("Accepted connection: " + connected_socket.getInetAddress().getHostAddress()
							+ " : " + connected_socket.getLocalPort());
				break;

			} catch (InterruptedException e) {
				System.err.println("Could not acquire Semaphore");
				continue;

			} catch (IOException e) {
				System.out.println("One of the servers refused connection.");
				return;
			}
		}

		try {
			input = new ObjectInputStream(connected_socket.getInputStream());
		} catch (IOException e) {
			if (Server.DEBUG)
				System.out.println("Could not open stream to socket.");
			return;
		}

		Node_Map.new_active_node(this);

		// A new node has come to alive
		Node_Exchange_Circulation.connection_lock.lock();
		Node_Exchange_Circulation.ready_connection_all.signal();
		Node_Exchange_Circulation.connection_lock.unlock();

		while (true) {
			try {
				Mutual_Exclusion myMsg = (Mutual_Exclusion) input.readObject();
				Mutual_Exclusion_CS.for_receive(myMsg);
			} catch (IOException e) {
				if (Server.DEBUG)
					System.out.println("Socket connection cut. " + e);
				System.out.println("Oops! Looks like one of the other servers went offline. I'll update my list.");
				Node_Map.deactive_node(this);
				return;
			} catch (ClassNotFoundException e) {
				if (Server.DEBUG)
					System.out.println("Illegal Message");
				continue;
			}
		}

	}

	public void stop() {
		Thread.currentThread().interrupt();
	}
}
