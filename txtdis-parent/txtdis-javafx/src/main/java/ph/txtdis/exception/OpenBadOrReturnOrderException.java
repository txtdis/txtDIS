package ph.txtdis.exception;

public class OpenBadOrReturnOrderException
	extends Exception {

	private static final long serialVersionUID = -2736815712342898065L;

	public OpenBadOrReturnOrderException(Long id) {
		super("Bad/Return Order No. " + id + "\nis still open");
	}
}
