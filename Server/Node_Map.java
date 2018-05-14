import java.util.ArrayList;
import java.util.HashMap;

public class Node_Map {
	private static ArrayList<Node_Attributes> created_nodes = new ArrayList<Node_Attributes>();
	private static ArrayList<Node_Exchange> active_node = new ArrayList<Node_Exchange>();
	private static HashMap<Integer, Node_Attributes> created_nodes_map = new HashMap<Integer, Node_Attributes>();

	public synchronized static void new_node(Node_Attributes node) {
		created_nodes.add(node);
		created_nodes_map.put(node.getID(), node);
	}

	public synchronized static void remove_node(Node_Attributes node) {
		created_nodes.remove(node);
	}

	public synchronized static Node_Attributes get_node(String ip, int port) {
		for (Node_Attributes nextNode : created_nodes) {
			if (Server.DEBUG)
				System.out.println("DEBUG::Comparing: " + nextNode.getNodeIP() + " with " + ip + ", and "
						+ nextNode.getNodePort() + " with " + port);
			if (nextNode.getNodeIP().equals(ip) && nextNode.getNodePort() == port)
				return nextNode;
		}

		return null;
	}

	public synchronized static Node_Attributes get_node_id(int id) {
		return created_nodes_map.get(id);
	}

	// Not pointer to actual node store
	public synchronized static ArrayList<Node_Attributes> get_all_nodes() {
		ArrayList<Node_Attributes> node_list = new ArrayList<Node_Attributes>(created_nodes);
		return node_list;
	}

	public synchronized static int get_active_node() {
		return active_node.size();
	}

	public synchronized static void new_active_node(Node_Exchange node) {
		active_node.add(node);
	}

	public synchronized static void deactive_node(Node_Exchange node) {
		active_node.remove(node);
	}

}
