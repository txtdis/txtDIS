package ph.txtdis.exception;

import ph.txtdis.dto.Billable;

public class AlreadyReferencedBookingIdException extends Exception {

	private static final long serialVersionUID = -1428885265318163309L;

	public AlreadyReferencedBookingIdException(String id, Billable b) {
		super("S/O No. " + id + "\nhas been used in\n" + b);
	}
}
