import java.io.IOException;
import java.net.Socket;

public class Server_Thread implements Runnable {

	public Server_Thread() {

	}

	@Override
	public void run() {
		Socket connected_socket = null;
		while (true) {
			try {
				connected_socket = Node_Exchange_Circulation.connection_next();

				if (Server.DEBUG && Server.VERBOSE)
					System.out
							.println("Accepted Client Connection: " + connected_socket.getInetAddress().getHostAddress()
									+ " : " + connected_socket.getLocalPort());
			} catch (IOException e) {
				System.out.println("One of the servers refused connection.");
				return;
			} catch (InterruptedException e) {
				continue;
			}

			if (connected_socket != null) {
				Operator tcp = new Operator();
				Thread t = new Thread(new I_O_Communication_Module(connected_socket, tcp));
				t.start();
			}
		}

	}
}
