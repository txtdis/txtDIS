package ph.txtdis.exception;

public class AlreadyAssignedPersonException extends Exception {

	private static final long serialVersionUID = -2596273848835338308L;

	public AlreadyAssignedPersonException(String username) {
		super(username + " has been assigned");
	}
}
