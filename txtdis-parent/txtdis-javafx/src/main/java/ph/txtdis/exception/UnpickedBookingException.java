package ph.txtdis.exception;

public class UnpickedBookingException extends Exception {

	private static final long serialVersionUID = 4124559920239749600L;

	public UnpickedBookingException(String reference, String id) {
		super(reference + " No. " + id + "\nhas not been picked.");
	}
}
