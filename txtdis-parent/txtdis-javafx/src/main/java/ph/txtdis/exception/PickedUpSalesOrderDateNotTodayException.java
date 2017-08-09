package ph.txtdis.exception;

public class PickedUpSalesOrderDateNotTodayException
	extends Exception {

	private static final long serialVersionUID = 7152560141778263735L;

	public PickedUpSalesOrderDateNotTodayException() {
		super("To-be-picked-up S/O's must be booked today");
	}
}
