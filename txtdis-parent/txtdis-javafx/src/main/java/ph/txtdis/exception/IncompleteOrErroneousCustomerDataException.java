package ph.txtdis.exception;

import ph.txtdis.dto.Customer;

public class IncompleteOrErroneousCustomerDataException extends Exception {

	private static final long serialVersionUID = -919387748993066310L;

	public IncompleteOrErroneousCustomerDataException(Customer c, String error) {
		super("No booking allowed if customers have incomplete/erroneous data;\n"//
				+ "#" + c.getId() + " " + c.getName() + "\n" + error);
	}
}
