package ph.txtdis.exception;

public class OpenPickedLoadOrderException
	extends Exception {

	private static final long serialVersionUID = -4956327798024447086L;

	public OpenPickedLoadOrderException(Long id) {
		super("L/O No. " + id + " has item-quantity variances.");
	}
}
