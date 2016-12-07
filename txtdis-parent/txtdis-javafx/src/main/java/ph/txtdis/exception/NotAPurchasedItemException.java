package ph.txtdis.exception;

import ph.txtdis.dto.Item;

public class NotAPurchasedItemException extends Exception {

	private static final long serialVersionUID = 710144457878059025L;

	public NotAPurchasedItemException(Item item) {
		super(item + "\nis NOT a purchased item.");
	}
}
