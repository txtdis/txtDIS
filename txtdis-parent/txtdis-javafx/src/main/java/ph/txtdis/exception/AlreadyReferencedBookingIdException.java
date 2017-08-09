package ph.txtdis.exception;

public class AlreadyReferencedBookingIdException
	extends Exception {

	private static final long serialVersionUID = -1428885265318163309L;

	public AlreadyReferencedBookingIdException(String id, String orderNo) {
		super("S/O No. " + id + "\nhas been used in\n" + orderNo);
	}
}
