import java.io.Serializable;

// Inventory contains product name and its quantity
public class Product_List implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8449018990251632233L;
	private String product_name;
	private int product_quantity;

	public Product_List(String name, int quantity) {
		product_name = name;
		product_quantity = quantity;
	}

	public String get_name() {
		return product_name;
	}

	public void set_name(String name) {
		this.product_name = name;
	}

	public int get_quantity() {
		return product_quantity;
	}

	public void set_quantity(int quantity) {
		this.product_quantity = quantity;
	}

	public String toString() {
		return product_name + " :: " + Integer.toString(product_quantity);
	}

	@Override
	public int hashCode() {
		return product_name.hashCode();
	}
}
