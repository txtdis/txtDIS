package ph.txtdis.exception;

public class UnauthorizedUserException
	extends Exception {

	private static final long serialVersionUID = -8431370059178958748L;

	public UnauthorizedUserException(String error) {
		super(error);
	}
}
