public class Client {
	public static final String system_name = "[Trading System <Team 17>] ";
	public static final String delimiter = " ";
	private static Functionality[] operations = {

			// Available functionalities
			// PURCHASE
			new Functionality() {
				@Override
				public String operation_name() {
					return "purchase";
				}

				@Override
				public void execute(String operation) {
					String username = null;
					String product_name = null;
					String quantity = null;

					String[] tokens = operation.split(delimiter);

					// Verify if the provided command is formatted properly
					try {

						username = tokens[1];
						product_name = tokens[2];
						quantity = tokens[3];

					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("There was not enough parameters provided.");
						System.out.print("\n" + system_name);
						return;
					}

					Communication_Module.operation_to_server(operation);
				}

				@Override
				public String help() {
					StringBuilder helpString = new StringBuilder("PURCHASE <username> <product name> <quantity>: ");
					helpString.append("If enough is present, purchases <quantity> amount of"
							+ "<product name> under the customer name <username>");
					return helpString.toString();
				}

			},

			// SEARCH
			new Functionality() {
				@Override
				public String operation_name() {
					return "search";
				}

				@Override
				public void execute(String operation) {
					String username = null;

					String[] tokens = operation.split(delimiter);

					try {
						username = tokens[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("There was not enough parameters provided.");
						System.out.print("\n" + system_name);
						return;
					}

					Communication_Module.operation_to_server(operation);
				}

				@Override
				public String help() {
					StringBuilder helpString = new StringBuilder("SEARCH <username>: ");
					helpString.append("Returns all orders placed by <username>.");
					return helpString.toString();
				}

			},

			// LIST
			new Functionality() {
				@Override
				public String operation_name() {
					return "list";
				}

				@Override
				public void execute(String operation) {
					/* There's no parameters needed for this command */
					Communication_Module.operation_to_server(operation);
				}

				@Override
				public String help() {
					String helpString = new String("LIST: Lists all the avaliable items in stock.");
					return helpString;
				}
			},

			// CANCEL
			new Functionality() {
				@Override
				public String operation_name() {
					return "cancel";
				}

				@Override
				public void execute(String operation) {
					String order_id = null;

					String[] tokens = operation.split(delimiter);
					try {
						order_id = tokens[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("There was not enough parameters provided.");
						System.out.print("\n" + system_name);
						return;
					}
					Communication_Module.operation_to_server(operation);
				}

				@Override
				public String help() {
					StringBuilder helpString = new StringBuilder("CANCEL <orderid>: ");
					helpString.append("If order <orderid> is present, the order is cancelled and quantity restocked.");
					return helpString.toString();
				}

			},

			// HELP
			new Functionality() {
				@Override
				public String operation_name() {
					return "help";
				}

				@Override
				public void execute(String operation) {
					System.out.println("Available Operations: ");
					for (Functionality a : operations) {
						if (!a.operation_name().equals("help"))
							System.out.println(a.help());
					}
					System.out.println(help());
					System.out.print("\n" + system_name);
				}

				@Override
				public String help() {
					StringBuilder helpString = new StringBuilder("HELP: ");
					helpString.append(system_name + "Prints out how to use every operation.");
					return helpString.toString();
				}
			} };

	public static void run() {
		System.out.print("\n" + system_name);
		boolean server_down = false;
		String previous = null;
		while (server_down || StdIO_Module.has_next_line()) {
			String operation = StdIO_Module.next_line();
			String[] tokens = operation.split(delimiter);
			Boolean no_operation = false;
			for (Functionality avaliable_operations : operations) {
				try {
					if (avaliable_operations.operation_name().equals(operation.split(delimiter)[0])) {
						avaliable_operations.execute(operation);
						no_operation = true;
					}
				} catch (Exception e) {
					System.out.println(system_name + "Please check input operation.");
					System.out.print("\n" + system_name);
					continue;
				}
			}

			if (!no_operation) {
				System.out.println(system_name + "No such operation found.\nType help for info.");
				System.out.print("\n" + system_name);
			}
			no_operation = false;

		}

	}


	public static void main(String[] args) {
		// Connection info
		Communication_Information.generate_communication_information();

		// Connection setup
		Communication_Information.establish_connection();

		// Ready
		run();
	}



}
