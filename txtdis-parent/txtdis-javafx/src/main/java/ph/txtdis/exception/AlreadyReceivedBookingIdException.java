package ph.txtdis.exception;

public class AlreadyReceivedBookingIdException extends Exception {

	private static final long serialVersionUID = -8123802272614410524L;

	public AlreadyReceivedBookingIdException(String reference, String id, Long receivingId) {
		super("R/R No. " + receivingId + " exists for\n" // 
				+ reference + " No. " + id);
	}
}
