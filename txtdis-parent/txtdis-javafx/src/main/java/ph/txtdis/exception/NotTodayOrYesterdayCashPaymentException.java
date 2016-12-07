package ph.txtdis.exception;

public class NotTodayOrYesterdayCashPaymentException extends Exception {

	private static final long serialVersionUID = -4430663872809922339L;

	public NotTodayOrYesterdayCashPaymentException() {
		super("Cash Payments must be dated today or yesterday.");
	}
}
