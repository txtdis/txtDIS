package ph.txtdis.exception;

public class NotTheAssignedCustomerSellerException extends Exception {

	private static final long serialVersionUID = 7399769514410624900L;

	public NotTheAssignedCustomerSellerException(String seller, String customer) {
		super("Only " + seller + "\ncan book for\n" + customer);
	}
}
