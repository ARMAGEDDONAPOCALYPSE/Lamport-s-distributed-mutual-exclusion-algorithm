import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


// Allows implementation of Lamport's Mutual Exclusion Algorithm
public class Mutual_Exclusion_CS {
	private static int sp = 1;

	static Comparator<Time_Stamp> time_stamp_comp = new Time_Stamp_Comparison();

	static PriorityQueue<Time_Stamp> request_queue = new PriorityQueue<Time_Stamp>(100, time_stamp_comp);

	static Semaphore priority_queue_semaphore = new Semaphore(sp);

	static AtomicInteger received_acknowledge = new AtomicInteger(0);

	static Time_Stamp request = null;

	// Condition Variable Synchronization
	private static final Object lock = new Object();

	// Request to CS, blocking until it is ready to be entered.
	public static void request_to_critical_section() {
		synchronized (lock) {
			// Increase clock as per the algorithm */
			Main_Node_Attributes.current_clock.increment();

			// Current clock and the request
			Causal_Clock stop_time = new Causal_Clock(Main_Node_Attributes.current_clock.get());

			Mutual_Exclusion request_msg = new Mutual_Exclusion(new Causal_Clock(stop_time.get()),
					Mutual_Exclusion.MESSAGE_REQUEST, Main_Node_Attributes.id);
			Time_Stamp request_stamp = new Time_Stamp(Main_Node_Attributes.id, new Causal_Clock(stop_time.get()));

			// Add request to the waiting queue
			while (true) {
				try {
					priority_queue_semaphore.acquire();
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}

			request_queue.add(request_stamp);
			request = request_stamp;

			priority_queue_semaphore.release();

			Node_Exchange_Circulation.broadcast(request_msg);
			// Block all until all acknowledges are back and it is on the top of the queue
			// Fault tolerance

			while (received_acknowledge.get() < (Node_Map.get_active_node() - 1)
					|| request_queue.peek().pid != Main_Node_Attributes.id) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
		}
	}

	public static void for_receive(Mutual_Exclusion me) {
		Main_Node_Attributes.current_clock.received_clock(new Causal_Clock(me.get_clock_value())); /* Max of clocks */

		if (me.get_message() == Mutual_Exclusion.MESSAGE_REQUEST) // A request message
		{
			synchronized (lock) {
				// Current clock and request
				Causal_Clock stop_time = new Causal_Clock(
						Main_Node_Attributes.current_clock.get()); /* Freeze the time right here */
				Mutual_Exclusion acknowledge_msg = new Mutual_Exclusion(new Causal_Clock(stop_time.get()),
						Mutual_Exclusion.MESSAGE_ACKNOWLEDGE, Main_Node_Attributes.id);

				Time_Stamp request_stamp = new Time_Stamp(me.get_id(), new Causal_Clock(me.get_clock_value()));
				// Add to queue
				while (true) {
					try {
						priority_queue_semaphore.acquire();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}

				request_queue.add(request_stamp);

				priority_queue_semaphore.release();

				Node_Exchange_Circulation.send_to_node(acknowledge_msg, me.get_id());
			}

		} else if (me.get_message() == Mutual_Exclusion.MESSAGE_ACKNOWLEDGE) {
			synchronized (lock) {
				received_acknowledge.incrementAndGet();
				lock.notify();
			}
		} else if (me.get_message() == Mutual_Exclusion.MESSAGE_RELEASE) {
			/* NOTE : Apply the necessary operations */
			if (me.Operation == Mutual_Exclusion.OPERATION_PURCHASE) {
				try {
					Server_Product_Manager.executeOrder(me.username, me.product_name, me.Parameter);
				} catch (Server_Product_Manager.No_This_Item noSuchItem) {
					/*
					 * NOTE: This shouldn't really be an issue due to it succeeding on the other
					 * end.
					 */
				} catch (Server_Product_Manager.Not_Enough_Item notEnoughItems) {
					/* NOTE: This shouldn't really be an issue due to FIFO. */
				}
			} else if (me.Operation == Mutual_Exclusion.OPERATION_CANCEL) {
				Server_Product_Manager.cancel_order(me.Parameter);
			}

			synchronized (lock) {

				while (true) {
					try {
						priority_queue_semaphore.acquire();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}

				// Delete the releasing process's request stamp
				Iterator<Time_Stamp> release_p_stamps = request_queue.iterator();

				while (release_p_stamps.hasNext()) {

					// Through all to find the stamp, and remove it from the request queue.
					Time_Stamp nextStamp = release_p_stamps.next();

					if (nextStamp.pid == me.get_id()) {
						release_p_stamps.remove();
						lock.notify();
					}

				}
				priority_queue_semaphore.release();
			}
		}
	}

	public static void release_critical_section(int Operation, String username, String product, int parameter) {
		// if not in CS, just return
		if (request == null)
			return;
		synchronized (lock) {
			Causal_Clock stop_time = new Causal_Clock(Main_Node_Attributes.current_clock.get());
			Mutual_Exclusion release_msg = new Mutual_Exclusion(new Causal_Clock(stop_time.get()),
					Mutual_Exclusion.MESSAGE_RELEASE, Main_Node_Attributes.id);

			// Set all operations to be performed
			release_msg.Operation = Operation;
			release_msg.username = username;
			release_msg.product_name = product;
			release_msg.Parameter = parameter;
			release_msg.set_order_number(Server_Product_Manager.order_manager.get_id());

			// Remove from queue once it is done
			while (true) {
				try {
					priority_queue_semaphore.acquire();
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}

			request_queue.remove(request);
			request = null;
			priority_queue_semaphore.release();

			Node_Exchange_Circulation.broadcast(release_msg);

		}
	}
}
