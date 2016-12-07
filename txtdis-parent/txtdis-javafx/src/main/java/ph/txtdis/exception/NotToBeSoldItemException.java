package ph.txtdis.exception;

import ph.txtdis.dto.Item;

public class NotToBeSoldItemException extends Exception {

	private static final long serialVersionUID = 8435771247321693955L;

	public NotToBeSoldItemException(Item item) {
		super(item + "\nis NOT to be sold.");
	}
}
