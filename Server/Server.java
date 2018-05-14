import java.util.Scanner;

public class Server {
	public static final boolean DEBUG = false;
	public static final boolean VERBOSE = false;
	private static String ip_port = ":";
	private static final String FILE_PATH = "C:\\Users\\ARMAG\\Desktop\\product.txt";

	public static void main(String[] args) {

		if (!DEBUG) {
			System.out.println("The server ID: ");
			Scanner sc = new Scanner(System.in);
			int myID = 0;
			try {
				myID = sc.nextInt();
			} catch (Exception e) {
				System.out.println("Please only enter a number!");
				System.exit(-1);
			}
			System.out.println("How many servers across all?");
			int server_num = 0;
			try {
				server_num = sc.nextInt();
			} catch (Exception e) {
				System.out.println("Please only enter a number!");
				System.exit(-1);
			}

			System.out.println("FILE PATH to be managed: ");
			// String inventoryPath = sc.next();
			System.out.println("File Path: [" + FILE_PATH + "]");

			// Update information
			Main_Node_Attributes.FILE_PATH = FILE_PATH;
			Main_Node_Attributes.id = myID;
			Main_Node_Attributes.total_number_nodes = server_num;

			System.out.println("Please enter server ip:port in order of 1st to last:");
			sc.nextLine();
			for (int i = 1; i <= server_num; i++) {
				// TODO: parse inputs to get the ips and ports of servers

				/* Read the server properties from STDIN */
				String input = sc.nextLine();
				String[] ip_address_port = input.split(ip_port);

				/* Get the new server's properties */
				int new_server_id = i;
				String new_server_ip = ip_address_port[0];
				int new_server_port = Integer.parseInt(ip_address_port[1]);

				/* A new server is being added */
				Node_Attributes new_node = new Node_Attributes(new_server_id, new_server_ip, new_server_port);

				/* NOTE: The current server is also added to the graph */

				/* NOTE: Set current server's identity */
				if (i == Main_Node_Attributes.id) {
					System.out.println("This is my ip and port according to server ID.");
					Main_Node_Attributes.ip = new_server_ip;
					Main_Node_Attributes.port = new_server_port;
				} else {
					System.out.println("External Server Registered!");
					Node_Map.new_node(new_node);
				}
			}
		} else {// DEBUG purpose
			Main_Node_Attributes.id = Integer.parseInt(args[0]); // The ID of the current server running

			if (Main_Node_Attributes.id == 1) {
				Main_Node_Attributes.ip = "127.0.0.1";
				Main_Node_Attributes.port = 1991;

				Main_Node_Attributes.FILE_PATH = FILE_PATH;
				Main_Node_Attributes.total_number_nodes = 3;

				// Node NodeOne = new Node(1, "127.0.0.1", 1991);
				Node_Attributes NodeTwo = new Node_Attributes(2, "127.0.0.1", 1992);
				Node_Attributes NodeThree = new Node_Attributes(3, "127.0.0.1", 1993);

				// Graph.newNode(NodeOne);
				Node_Map.new_node(NodeTwo);
				Node_Map.new_node(NodeThree);

			} else if (Main_Node_Attributes.id == 2) {
				Main_Node_Attributes.ip = "127.0.0.1";
				Main_Node_Attributes.port = 1992;

				Main_Node_Attributes.FILE_PATH = FILE_PATH;
				Main_Node_Attributes.total_number_nodes = 3;

				Node_Attributes NodeOne = new Node_Attributes(1, "127.0.0.1", 1991);
				// Node NodeTwo = new Node(2, "127.0.0.1", 1992);
				Node_Attributes NodeThree = new Node_Attributes(3, "127.0.0.1", 1993);

				Node_Map.new_node(NodeOne);
				// Graph.newNode(NodeTwo);
				Node_Map.new_node(NodeThree);
			} else if (Main_Node_Attributes.id == 3) {
				Main_Node_Attributes.ip = "127.0.0.1";
				Main_Node_Attributes.port = 1993;

				Main_Node_Attributes.FILE_PATH = FILE_PATH;
				Main_Node_Attributes.total_number_nodes = 3;

				Node_Attributes NodeOne = new Node_Attributes(1, "127.0.0.1", 1991);
				Node_Attributes NodeTwo = new Node_Attributes(2, "127.0.0.1", 1992);
				// Node NodeThree = new Node(3, "127.0.0.1", 1993);

				Node_Map.new_node(NodeOne);
				Node_Map.new_node(NodeTwo);
				// Graph.newNode(NodeThree);
			} else {
				System.err.println("DEBUG: Server ID from 1 - 3");
			}

		}

		System.out.println(
				"Server initialization complete.\nWaiting for peers...\nSending Lamport" + "'s Message and Clock");
		// TODO: start server socket to communicate with clients and other servers

		/* NOTE: Bind this server to the server's port and IP location */
		Node_Exchange_Circulation.BindServerSocket(Main_Node_Attributes.port);

		/* NOTE: Start listening to all the other server's requests for connections. */
		Node_Exchange_Circulation.listening_to_active_nodes(Node_Map.get_all_nodes());

		/* NOTE: Start sending out connection requests to everyone */
		Node_Exchange_Circulation.connecting_to_active_nodes(Node_Map.get_all_nodes());

		/* NOTE: Wait until all connections are stable before continuing */
		Node_Exchange_Circulation.waiting_for_active_connections_to_set(Node_Map.get_all_nodes().size());

		/*
		 * NOTE: Working under the assumption that clients do not connect until all
		 * servers connect
		 */
		System.out.println(
				"All Servers have been established.\nConnections are stable...\nUpdating product list...\nStarting service");
		/*
		 * NOTE: We parse the inventory file here and update the array with the items
		 * that the inventory file features
		 */
		Update_Product_List.start_update(Main_Node_Attributes.FILE_PATH);

		System.out.println("All Set...");
		// TODO: handle request from client
		try {
			Thread TCPMaster = new Thread(new Server_Thread());
			TCPMaster.start();
			TCPMaster.join();
		} catch (InterruptedException g) {
			// TODO: Handle this case
			System.err.println("Server.java InterruptedException");
		}

	}
}
