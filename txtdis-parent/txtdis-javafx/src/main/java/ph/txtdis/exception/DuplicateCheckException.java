package ph.txtdis.exception;

public class DuplicateCheckException extends Exception {

	private static final long serialVersionUID = -4008903600748202903L;

	public DuplicateCheckException(String bank, Long checkId, Long collectionId) {
		super(bank + " Check No. " + checkId + "\nhas been used in\n" + "Collections Record No. " + collectionId);
	}
}
