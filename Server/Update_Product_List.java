import java.io.FileNotFoundException;
import java.io.IOException;

public class Update_Product_List {

	public static void start_update(String file_path) {
		Product_List_Reader reader = null;
		try {
			reader = new Product_List_Reader(file_path);
		} catch (FileNotFoundException e) {
			System.out.println("File was not found.");
			System.exit(-1);
		}

		while (true) {
			try // Try to read in the next item from the product list file.
			{
				Product_List nextItem = reader.next_product();
				if (nextItem != null) {
					Server_Product_Manager.insert_item(nextItem.get_name(), nextItem);
				}
			} catch (Product_List_Reader.End_of_line SystemOutOfItems) {
				break;
			} catch (IOException errFile) {
				System.out.println("IO Exception while reading product list file.");
				System.exit(-1);
			}
		}
	}

}
