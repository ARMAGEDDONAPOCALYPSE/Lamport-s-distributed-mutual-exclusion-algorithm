import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

// Establishing server-client connection
// Sending query operation to servers
// Check all server status, remove node if down

public class Communication_Module {
	static String previous;
	static Boolean server_down = false;

	public static void operation_to_server(String input) {
		Server_Node current_node = Communication_Information.get_current_node();
		int main_server = Communication_Information.get_main_server();

		while (true) {
			String operation = (server_down) ? previous : input;
			server_down = false;

			try {
				Socket client_socket = new Socket(current_node.get_node_IP(), current_node.get_node_port());
				DataOutputStream to_server = new DataOutputStream(client_socket.getOutputStream());
				BufferedReader from_server = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
				to_server.writeBytes(operation + "\n");

				char[] inArray = new char[10240];
				int size = from_server.read(inArray);
				String server_response = new String(inArray, 0, size);
				client_socket.close();

				System.out.println(server_response.toString());
				System.out.print("\n>> ");
				break;
			} catch (Exception e) {
				// Remove the server node if it is down
				Server_Node_Map.remove_node(current_node);

				// All servers are down
				if (Server_Node_Map.get_all_nodes().size() == 0) {
					System.out.println("All servers are down.");
					System.exit(0);
				}

				main_server = (main_server + 1) % Server_Node_Map.get_all_nodes().size();
				current_node = Server_Node_Map.get_all_nodes().get(main_server);
				previous = new String(operation);
				server_down = true;
			}
		}
	}

}
