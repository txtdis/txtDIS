package ph.txtdis.exception;

public class InvalidPhoneException
	extends Exception {

	private static final long serialVersionUID = -2191487596665917920L;

	public InvalidPhoneException(String ph) {
		super(ph + " is an invalid phone number");
	}
}
