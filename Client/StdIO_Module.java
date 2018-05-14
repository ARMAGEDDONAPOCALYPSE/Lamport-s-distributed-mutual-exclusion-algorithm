import java.util.Scanner;

// Invalid input number
public class StdIO_Module {
	private static Scanner sc = new Scanner(System.in);

	public enum Invalid_Input {
		INVALID_USER_INPUT(-1);

		private int num_val;

		Invalid_Input(int num_val) {
			this.num_val = num_val;
		}

		public int getNumVal() {
			return num_val;
		}

	}

	public static int get_next_integer() {
		int value = -1;
		try {
			value = sc.nextInt();
		} catch (Exception e) {
			System.err.print("Please only enter a valid number. [STDIO]");
			System.exit(Invalid_Input.INVALID_USER_INPUT.getNumVal());
		}
		return value;
	}

	public static Boolean has_next_line() {
		return sc.hasNextLine();
	}

	public static String next_line() {
		return sc.nextLine();
	}
}
