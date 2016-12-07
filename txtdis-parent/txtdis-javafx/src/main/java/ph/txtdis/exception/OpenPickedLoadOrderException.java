package ph.txtdis.exception;

public class OpenPickedLoadOrderException extends Exception {

	private static final long serialVersionUID = -4956327798024447086L;

	public OpenPickedLoadOrderException(Long id) {
		super("All picked load orders prior to cut-off\n"//
				+ "must be closed to continue;\n"//
				+ "L/O No. " + id + " has item-quantity variances.");
	}
}
