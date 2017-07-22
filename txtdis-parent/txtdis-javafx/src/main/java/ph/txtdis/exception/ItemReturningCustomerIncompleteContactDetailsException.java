package ph.txtdis.exception;

public class ItemReturningCustomerIncompleteContactDetailsException extends Exception {

	private static final long serialVersionUID = 2874806720155756184L;

	public ItemReturningCustomerIncompleteContactDetailsException(String customer) {
		super(customer + " has incomplete contact details.");
	}
}
