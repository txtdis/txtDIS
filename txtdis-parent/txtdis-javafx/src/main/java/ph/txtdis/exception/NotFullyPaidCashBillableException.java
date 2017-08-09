package ph.txtdis.exception;

public class NotFullyPaidCashBillableException
	extends Exception {

	private static final long serialVersionUID = 6376789991394072985L;

	public NotFullyPaidCashBillableException(String id) {
		super("No booking if CODs have NOT been fully paid;\n"//
			+ "S/I(D/R) No. " + id + " is still not");
	}
}
