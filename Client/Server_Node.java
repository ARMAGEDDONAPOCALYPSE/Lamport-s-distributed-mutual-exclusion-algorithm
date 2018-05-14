//Node attributes

public class Server_Node {
	private int id;
	private String ip;
	private int port;

	public Server_Node(int id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}

	// The fields cannot be set once created
	public int get_node_ID() {
		return id;
	}

	public String get_node_IP() {
		return ip;
	}

	public int get_node_port() {
		return port;
	}

	@Override
	public boolean equals(Object compare) {
		if (((Server_Node) compare).get_node_IP().equals(ip)
				&& ((Server_Node) compare).get_node_port() == port)
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return port;
	}
}
