package ph.txtdis.exception;

public class NotSellableItemException extends Exception {

	private static final long serialVersionUID = -4513835196103359411L;

	public NotSellableItemException(String item) {
		super(item + "\nis free,\ntherefore, not sellable");
	}
}
