import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;

//	Object Output Stream and Semaphore (acquire and release)
// 	Blocking call for stream

public class ObjectOutputStream_Semaphore {
	private ObjectOutputStream output;
	private Semaphore semaphore_stream;
	private int sp = 1;

	public ObjectOutputStream_Semaphore(ObjectOutputStream stream) {
		output = stream;
		semaphore_stream = new Semaphore(sp);
	}

	public ObjectOutputStream acquire_stream() throws InterruptedException {
		semaphore_stream.acquire();
		return output;
	}

	public void release_stream() {
		semaphore_stream.release();
	}

}
