//Current node attributes

public class Main_Node_Attributes {
	// Current node's ID number
	static int id;

	// Current node's IP
	static String ip;

	// Current node's Port number
	static int port;

	// Inventory file path
	static String FILE_PATH;

	
	// Global properties:

	// Total number of nodes
	static int total_number_nodes;
	static int number_nodes_alive;

	// Mutual Exclusion Variables
	static Causal_Clock current_clock = new Causal_Clock(0);

}
