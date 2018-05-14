import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server_Product_Manager {
	static HashMap<String, Product_List> server_product_list = new HashMap<String, Product_List>();
	static User_Order_Manager order_manager = new User_Order_Manager();

	static Server_Product_Manager this_manager = new Server_Product_Manager();

	public class No_This_Item extends Exception {

		/**
		 * No item exception here
		 */
		private static final long serialVersionUID = -8618038090876114945L;
	}

	public class Not_Enough_Item extends Exception {

		/**
		 * No enough item exception
		 */
		private static final long serialVersionUID = -4880878381626411166L;
	}

	// Only be called on INIT, Does work after being connected with client
	public static synchronized void insert_item(String name, Product_List item) {
		// System.out.println(itemName);
		server_product_list.put(name, item);
	}

	public static synchronized User_Order_Manager.Order executeOrder(String username, String product_name, int quantity)
			throws No_This_Item, Not_Enough_Item {
		Product_List check_items = server_product_list.get(product_name);
		if (check_items == null) {
			throw this_manager.new No_This_Item();
		}

		if (check_items.get_quantity() - quantity < 0) {
			throw this_manager.new Not_Enough_Item();
		}
		// Process Succ
		check_items.set_quantity(check_items.get_quantity() - quantity);
		// Place order
		User_Order_Manager.Order ok = order_manager.generate_new_user_order(username, product_name, quantity);

		return ok;
	}

	public static synchronized boolean cancel_order(int id) {
		User_Order_Manager.Order which_order = order_manager.get_order_by_id(id);
		if (which_order == null) {
			Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_CANCEL, null, null,
					id); /* NOTE: Let everyone else know about the changes made */
			return false;
		}

		int quantity = which_order.quantity;
		String item = which_order.name;

		Product_List ItemToUpdate = server_product_list.get(item);
		ItemToUpdate.set_quantity(ItemToUpdate.get_quantity() + quantity);
		boolean success = order_manager.remove_order_by_id(id);
		if (Server.DEBUG)
			System.out.println("Cancel order: " + success + " at time: " + System.currentTimeMillis());
		assert (success == true);

		return true;
	}

	public static synchronized ArrayList<Product_List> get_all_product_items() {
		ArrayList<Product_List> product_list = new ArrayList<Product_List>(server_product_list.values());
		return product_list;
	}

	public static synchronized void update_user_order(HashMap<String, Product_List> ParamServerInventory,
			HashMap<String, ArrayList<User_Order_Manager.Order>> users,
			HashMap<User_Order_Manager.Order, User_Order_Manager.User> orders,
			HashMap<Integer, User_Order_Manager.Order> generate_order, AtomicInteger current_order) {
		server_product_list = new HashMap<String, Product_List>(ParamServerInventory);
		order_manager.update_user_order(users, orders, generate_order);
		order_manager.set_order_id(current_order);
		if (Server.DEBUG)
			System.out.println("***DEBUG: Updated users and orders at time: " + System.currentTimeMillis());
	}
	
	public static ArrayList<User_Order_Manager.Order> print_orders_by_user(String username) {
		return order_manager.get_order_by_user(username);
	}

}
