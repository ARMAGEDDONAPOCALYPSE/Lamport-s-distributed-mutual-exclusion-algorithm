import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

// Socket and semaphore (acquire and release)
// Blocking call for socket
public class ServerSocket_Semaphore {
	private ServerSocket server_socket;
	private Semaphore semaphore_socket;
	private int sp = 1;

	public ServerSocket_Semaphore(int port) throws IOException {
		server_socket = new ServerSocket(port);
		semaphore_socket = new Semaphore(sp);
	}

	public ServerSocket acquire_socket() throws InterruptedException {
		semaphore_socket.acquire();
		return server_socket;
	}

	public void release_socket() {
		semaphore_socket.release();
	}
}
