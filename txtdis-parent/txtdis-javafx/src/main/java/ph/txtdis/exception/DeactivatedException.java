package ph.txtdis.exception;

public class DeactivatedException
	extends InvalidException {

	private static final long serialVersionUID = -2617612081995129258L;

	public DeactivatedException(String name) {
		super(name + "\nhas been deactivated");
	}
}
