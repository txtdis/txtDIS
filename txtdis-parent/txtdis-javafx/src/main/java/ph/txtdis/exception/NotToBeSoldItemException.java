package ph.txtdis.exception;

public class NotToBeSoldItemException extends Exception {

	private static final long serialVersionUID = 8435771247321693955L;

	public NotToBeSoldItemException(String item) {
		super(item + "\nis NOT to be sold.");
	}
}
