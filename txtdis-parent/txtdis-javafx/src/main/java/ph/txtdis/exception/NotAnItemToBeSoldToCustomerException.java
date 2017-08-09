package ph.txtdis.exception;

public class NotAnItemToBeSoldToCustomerException
	extends Exception {

	private static final long serialVersionUID = 5868566649654122201L;

	public NotAnItemToBeSoldToCustomerException(String item, String customer) {
		super(item + "\nis not to be sold to\n" + customer);
	}
}
