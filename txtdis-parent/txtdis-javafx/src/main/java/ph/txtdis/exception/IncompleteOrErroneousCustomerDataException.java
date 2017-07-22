package ph.txtdis.exception;

public class IncompleteOrErroneousCustomerDataException extends Exception {

	private static final long serialVersionUID = -919387748993066310L;

	public IncompleteOrErroneousCustomerDataException(Long customerId, String customerName, String error) {
		super("No booking allowed if customers have incomplete/erroneous data;\n"//
				+ "#" + customerId + " " + customerName + "\n" + error);
	}
}
