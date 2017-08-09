package ph.txtdis.exception;

public class NotAPurchasedItemException
	extends Exception {

	private static final long serialVersionUID = 710144457878059025L;

	public NotAPurchasedItemException(String item) {
		super(item + "\nis NOT a purchased item.");
	}
}
