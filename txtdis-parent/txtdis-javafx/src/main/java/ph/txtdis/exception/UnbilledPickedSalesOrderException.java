package ph.txtdis.exception;

public class UnbilledPickedSalesOrderException extends Exception {

	private static final long serialVersionUID = 4671867557387899786L;

	public UnbilledPickedSalesOrderException(Long id) {
		super("Picked S/O No. " + id + "\nis still unbilled");
	}
}
