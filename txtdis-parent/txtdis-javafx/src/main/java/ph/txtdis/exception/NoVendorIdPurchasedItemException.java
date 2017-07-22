package ph.txtdis.exception;

public class NoVendorIdPurchasedItemException extends InvalidException {

	private static final long serialVersionUID = 710144457878059025L;

	public NoVendorIdPurchasedItemException(String item) {
		super(item + "\nis purchased,\nbut has no vendor ID");
	}
}
