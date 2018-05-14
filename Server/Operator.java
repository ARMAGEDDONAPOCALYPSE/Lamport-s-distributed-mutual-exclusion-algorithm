import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Operator {
	String delimiter = new String(" ");

	public Operator() {

	}

	public class Operation_Not_Found extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1341469622177903858L;
		int error = 0;

		public Operation_Not_Found(int number) {
			super();
			error = number;
		}

		public Operation_Not_Found(String message, int number) {
			super(message);
			error = number;
		}

		public Operation_Not_Found(String message, Throwable cause) {
			super(message, cause);
		}

		public Operation_Not_Found(Throwable cause) {
			super(cause);
		}
	}

	public String execute_operation(String op) throws Operation_Not_Found {
		StringTokenizer operation_tokens = new StringTokenizer(op, delimiter);
		String operation = null;
		User_Order_Manager.Order new_order = null;
		try {
			operation = operation_tokens.nextToken();
		} catch (NoSuchElementException e) {
			throw new Operation_Not_Found("Empty operation.", 1);
		}

		if (operation == null) {
			throw new Operation_Not_Found("Empty operation.", 1);
		}

		
		if (operation.equals("purchase")) {
			String username = null;
			String product_name = null;
			String quantity = null;
			try {
				username = operation_tokens.nextToken();
				product_name = operation_tokens.nextToken();
				quantity = operation_tokens.nextToken();
			} catch (NoSuchElementException e) {
				throw new Operation_Not_Found("Not enough argvs, Please Check help for info", 3);
			}

			try {
				// Request to CS for operations
				Mutual_Exclusion_CS.request_to_critical_section();
				new_order = Server_Product_Manager.executeOrder(username, product_name, Integer.parseInt(quantity));
				// Release to inform every server knows the update
				Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_PURCHASE, username,
						product_name, Integer.parseInt(quantity));
			} catch (Server_Product_Manager.No_This_Item e) {
				// Exception there is no this item, inform every server no change happened
				Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_TYPE_NOCHANGE, username,
						product_name, Integer.parseInt(quantity));
				return "The Product <Not Available> \nCheck list";
			} catch (Server_Product_Manager.Not_Enough_Item g) {
				// Exception there is not enough item, inform every server no change happened
				Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_TYPE_NOCHANGE, username,
						product_name, Integer.parseInt(quantity));
				return "The Product <Not Available>- <Not enough items> \nCheck list";
			} catch (NumberFormatException h) {
				// Exception not identified number, inform every server no change happened
				Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_TYPE_NOCHANGE, username,
						product_name, Integer.parseInt(quantity));
				return "Please enter a valid number";
			}

			return "You order has been successfully placed, [(" + new_order.id + ") <" + new_order.user.username + "> <"
					+ new_order.name + "> :" + new_order.quantity + "]";
		} else if (operation.equals("cancel")) {
			String order_id = null;
			try {
				order_id = operation_tokens.nextToken();
			} catch (NoSuchElementException e) {
				throw new Operation_Not_Found("Not enough argvs", 3);
			}

			try {
				// Request to enter CS
				Mutual_Exclusion_CS.request_to_critical_section();
				boolean result = Server_Product_Manager.cancel_order(Integer.parseInt(order_id));
				if (result) {
					Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_CANCEL, null, null,
							Integer.parseInt(order_id));
					return "[Order (" + order_id + ") is canceled.]";
				} else {
					Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_TYPE_NOCHANGE, null, null,
							0);
					return "[Order (" + order_id + ") NOT FOUND]";
				}
			} catch (NumberFormatException h) {
				Mutual_Exclusion_CS.release_critical_section(Mutual_Exclusion.OPERATION_TYPE_NOCHANGE, null, null, 0);
				return "Please enter a valid number";
			}

		} else if (operation.equals("search")) {
			String username = null;
			try {
				username = operation_tokens.nextToken();
			} catch (NoSuchElementException e) {
				throw new Operation_Not_Found("Not enough argvs", 3);
			}

			ArrayList<User_Order_Manager.Order> user_order = Server_Product_Manager.print_orders_by_user(username);

			if (user_order == null || user_order.size() == 0) {
				return "No order FOUND for " + username + "\nPlease place order with [purchase].";
			} else {
				// Format order for given username
				StringBuilder return_order = new StringBuilder();

				for (int i = 0; i < user_order.size(); i++) {
					User_Order_Manager.Order next_order = user_order.get(i);
					return_order.append(next_order.id);
					return_order.append(", ");
					return_order.append(next_order.name);
					return_order.append(", ");
					return_order.append(next_order.quantity);
					if (i < user_order.size() - 1)
						return_order.append("\n");
				}

				return return_order.toString();
			}

		} else if (operation.equals("list")) {
			// TODO: List Format product name and quant
			StringBuilder return_product_list = new StringBuilder();

			ArrayList<Product_List> items = Server_Product_Manager.get_all_product_items();

			for (int i = 0; i < items.size(); i++) {
				Product_List nextItem = items.get(i);
				return_product_list.append(nextItem.get_name());
				return_product_list.append(" ");
				return_product_list.append(nextItem.get_quantity());
				if (i < items.size() - 1)
					return_product_list.append("\n");
			}

			return return_product_list.toString();
		} else {
			throw new Operation_Not_Found("Operation NOT FOUND.", 2);
		}

	}

}
