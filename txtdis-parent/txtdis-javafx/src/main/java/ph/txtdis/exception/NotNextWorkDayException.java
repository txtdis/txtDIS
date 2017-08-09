package ph.txtdis.exception;

public class NotNextWorkDayException
	extends Exception {

	private static final long serialVersionUID = 6963002563936579349L;

	public NotNextWorkDayException() {
		super("Date must be the next work day");
	}
}
