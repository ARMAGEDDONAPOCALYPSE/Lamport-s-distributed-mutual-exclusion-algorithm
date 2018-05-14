import java.io.*;
import java.util.StringTokenizer;

//	Reading product file and deal with exceptions

public class Product_List_Reader {
	public String file_name;
	BufferedReader reader = null;
	String space_delimiter = " ";

	public Product_List_Reader(String filename) throws FileNotFoundException {
		file_name = filename;
		reader = new BufferedReader(new FileReader(filename));
	}

	public Product_List_Reader(String filename, String delimiter) {
		file_name = filename;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("\nFile not found.");
		}
		space_delimiter = delimiter;
	}

	public Product_List next_product() throws IOException, End_of_line {
		String line = reader.readLine();

		if (line == null) {
			throw new End_of_line("Reached end of inventory file.");
		}

		StringTokenizer tokenizer = new StringTokenizer(line, space_delimiter);

		if (line.equals("")) {
			return null;
		} else if (tokenizer.countTokens() != 2) {
			return null;
		}

		return new Product_List(tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()));

	}

	public class End_of_line extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8020681491215801763L;

		// The reader has reached the end of file
		public End_of_line() {
			super();
		}

		public End_of_line(String msg) {
			super(msg);
		}

		public End_of_line(String msg, Throwable t) {
			super(msg, t);
		}

		public End_of_line(Throwable t) {
			super(t);
		}

	}

}
