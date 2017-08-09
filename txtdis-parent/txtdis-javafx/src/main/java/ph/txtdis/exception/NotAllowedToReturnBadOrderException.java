package ph.txtdis.exception;

public class NotAllowedToReturnBadOrderException
	extends Exception {

	private static final long serialVersionUID = -3135096519524601477L;

	public NotAllowedToReturnBadOrderException(String customer) {
		super(customer + "\nis not allowed to make\nBad Order Returns at this time.");
	}
}
