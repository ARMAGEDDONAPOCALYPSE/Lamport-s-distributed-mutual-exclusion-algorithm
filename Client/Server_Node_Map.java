import java.util.ArrayList;

//Concurrency for all server nodes
public class Server_Node_Map {
	private static ArrayList<Server_Node> server_nodes = new ArrayList<Server_Node>();

	public synchronized static Server_Node get_node(String IP, int port) {
		for (Server_Node nextNode : server_nodes) {
			if (nextNode.get_node_IP().equals(IP) && nextNode.get_node_port() == port)
				return nextNode;
		}

		return null;
	}

	public synchronized static void new_node(Server_Node new_node) {
		server_nodes.add(new_node);
	}

	public synchronized static void remove_node(Server_Node remove_node) {
		server_nodes.remove(remove_node);
	}

	// REMEMBER: Does not return pointer to actual node store
	public synchronized static ArrayList<Server_Node> get_all_nodes() {
		ArrayList<Server_Node> return_list = new ArrayList<Server_Node>(server_nodes);
		return return_list;
	}

}
