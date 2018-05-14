import java.io.Serializable;

//	lamport's clock 
//	

public class Causal_Clock implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8954543360998473876L;
	private int clock_value;

	public Causal_Clock(int initial_value) {
		clock_value = initial_value;
	}

	public synchronized void increment() {
		clock_value++;
	}

	public synchronized void received_clock(Causal_Clock clock) {
		clock_value = Math.max(clock_value, clock.clock_value);
		clock_value++;
	}

	public synchronized int get() {
		return clock_value;
	}
}
