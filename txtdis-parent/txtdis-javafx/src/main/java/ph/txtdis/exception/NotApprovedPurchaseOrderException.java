package ph.txtdis.exception;

public class NotApprovedPurchaseOrderException
	extends Exception {

	private static final long serialVersionUID = 7399769514410624900L;

	public NotApprovedPurchaseOrderException(Long id) {
		super("P/O No. " + id + "\nis NOT approved");
	}
}
