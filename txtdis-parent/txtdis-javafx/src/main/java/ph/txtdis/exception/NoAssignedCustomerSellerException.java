package ph.txtdis.exception;

public class NoAssignedCustomerSellerException extends Exception {

	private static final long serialVersionUID = 2883475447261686961L;

	public NoAssignedCustomerSellerException(String customer) {
		super("No assigned seller for\n" + customer);
	}
}
