import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

// For Lamport's Mutual Exclusion Algorithm, sending message along with clock
public class Mutual_Exclusion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4835160189841883195L;
	public static final int MESSAGE_REQUEST = 0;
	public static final int MESSAGE_ACKNOWLEDGE = 1;
	public static final int MESSAGE_RELEASE = 2;

	public static final int OPERATION_PURCHASE = 0;
	public static final int OPERATION_CANCEL = 1;
	public static final int OPERATION_TYPE_NOCHANGE = 2;

	private Causal_Clock clock;
	private int message;

	private int pid;

	int Operation;
	String username;
	String product_name;
	int Parameter; // Either quantity, or order number

	// An int value that may be updated atomically
	private AtomicInteger order_number;

	public AtomicInteger get_order_number() {
		return order_number;
	}

	public void set_order_number(AtomicInteger num) {
		order_number = num;
	}

	public Mutual_Exclusion(Causal_Clock clock, int msg, int pid) {
		this.message = msg;
		this.clock = clock;
		this.pid = pid;
	}

	public int get_clock_value() {
		return clock.get();
	}

	public int get_message() {
		return message;
	}

	public int get_id() {
		return pid;
	}

}
