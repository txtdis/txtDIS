package ph.txtdis.exception;

public class InsufficientBadOrderAllowanceException extends Exception {

	private static final long serialVersionUID = -75168436938104495L;

	public InsufficientBadOrderAllowanceException() {
		super("Not enough bad order allowance\n"//
				+ "for this item quantity");
	}
}
