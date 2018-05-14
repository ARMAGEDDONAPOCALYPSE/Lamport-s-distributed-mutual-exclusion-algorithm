// Setup server-client communication info

public class Communication_Information {
	// The total number of existing servers
	private static int total_number_servers;

	// The main server to communicate to
	private static int main_server = 0;
	static Server_Node current_node;

	private static void askForServersNumbers() {
		System.out.println("Please enter the number of servers that exist:");
		total_number_servers = StdIO_Module.get_next_integer();
	}

	private static void getPeerServers() {
		StdIO_Module.next_line();

		for (int i = 0; i < total_number_servers; i++) {
			System.out.println("Please enter Server " + i + "'s IP: ");
			String input = StdIO_Module.next_line();
			String[] ipPortSplit = input.split(":");

			/* Get the new server's properties */
			int newServerID = i;
			String newServerIP = ipPortSplit[0];
			int newServerPort = Integer.parseInt(ipPortSplit[1]);

			/* A new server is being added */
			Server_Node newNode = new Server_Node(newServerID, newServerIP, newServerPort);

			Server_Node_Map.new_node(newNode);
		}
		current_node = Server_Node_Map.get_all_nodes().get(0);
	}

	public static void generate_communication_information() {
		askForServersNumbers();
		getPeerServers();
	}

	public static void establish_connection() {
		int main_server = 0;
		Server_Node_Map.get_all_nodes().get(main_server);
	}

	public static Server_Node get_current_node() {
		return current_node;
	}

	public static int get_main_server() {
		return main_server;
	}
}
