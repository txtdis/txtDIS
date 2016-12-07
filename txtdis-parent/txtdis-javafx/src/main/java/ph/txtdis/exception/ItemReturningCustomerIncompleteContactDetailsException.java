package ph.txtdis.exception;

import ph.txtdis.dto.Customer;

public class ItemReturningCustomerIncompleteContactDetailsException extends Exception {

	private static final long serialVersionUID = 2874806720155756184L;

	public ItemReturningCustomerIncompleteContactDetailsException(Customer c) {
		super("Item-returning customers\nmust have complete contact details;\n" + c + "'s are not");
	}
}
