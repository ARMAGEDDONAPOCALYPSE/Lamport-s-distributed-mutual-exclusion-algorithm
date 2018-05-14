import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class User_Order_Manager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1581520569044494878L;
	private int order_range = 100;
	HashMap<String, ArrayList<Order>> all_users = new HashMap<String, ArrayList<Order>>();
	HashMap<Order, User> all_orders = new HashMap<Order, User>();
	HashMap<Integer, Order> generate_order = new HashMap<Integer, Order>();
	private AtomicInteger order_id = new AtomicInteger(order_range);

	public class Order implements Serializable {
		/**
		 * Order attributes
		 */
		private static final long serialVersionUID = -559002672052796858L;
		public int id;
		public String name;
		public int quantity;
		public User user;

		public Order(int oid) {
			id = oid;
		}

		public Order(int oid, String prod, int quant, User person) {
			id = oid;
			name = prod;
			quantity = quant;
			user = person;
		}

		@Override
		public int hashCode() {
			return id;
		}
	}

	public class User implements Serializable {
		/**
		 * User attributes
		 */
		private static final long serialVersionUID = 7979810021782589652L;
		String username;

		public User(String name) {
			username = name;
		}

		@Override
		public int hashCode() {
			return username.hashCode();
		}

	}

	public AtomicInteger get_id() {
		return order_id;
	}

	public void set_order_id(AtomicInteger id) {
		order_id = new AtomicInteger(id.get());
	}

	public ArrayList<Order> get_order_by_user(String username) {
		return all_users.get(username);
	}

	public synchronized User get_user_by_order(Order o) {
		return all_orders.get(o);
	}

	private int generate_order_id() {
		return order_id.getAndIncrement();
	}

	public Order get_order_by_id(int id) {
		return generate_order.get(new Integer(id));
	}

	public boolean remove_order_by_id(int id) {
		Order o = generate_order.get(id);
		if (o == null)
			return false;
		generate_order.remove(id);

		User u = all_orders.get(o);
		if (u == null)
			return false;
		all_orders.remove(o);

		ArrayList<Order> UsersOrders = all_users.get(u.username);
		UsersOrders.remove(o);

		return true;

	}

	public synchronized Order generate_new_user_order(String username, String product_name, int quantity) {
		User current_user = new User(username);

		ArrayList<Order> user_orders = all_users.get(username);

		if (user_orders == null) {
			user_orders = new ArrayList<Order>();
			all_users.put(username, user_orders); // add new user
		}

		Order current_order = new Order(generate_order_id(), product_name, quantity, current_user);
		user_orders.add(current_order);
		all_orders.put(current_order, current_user);
		generate_order.put(current_order.id, current_order);
		return current_order;
	}

	public void update_user_order(HashMap<String, ArrayList<Order>> all_users, HashMap<Order, User> all_orders,
			HashMap<Integer, Order> gen_order) {
		this.all_users = new HashMap<String, ArrayList<Order>>(all_users);
		this.all_orders = new HashMap<Order, User>(all_orders);
		this.generate_order = new HashMap<Integer, Order>(gen_order);
	}

}
