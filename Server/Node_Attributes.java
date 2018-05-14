// Single server attributes

public class Node_Attributes {
	private int id;
	private String ip;
	private int port;

	public Node_Attributes(int id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}

	public int getID() {
		return id;
	}

	public String getNodeIP() {
		return ip;
	}

	public int getNodePort() {
		return port;
	}

	@Override
	public boolean equals(Object compareTo) {
		if (((Node_Attributes) compareTo).getNodeIP().equals(ip) 
				&& ((Node_Attributes) compareTo).getNodePort() == port)
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return port;
	}

	public String toString() {
		return new String("::: ID: " + id + " (IP: " + ip + ", Port: " + port + ") :::");
	}
}
