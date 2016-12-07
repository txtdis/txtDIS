package ph.txtdis.exception;

public class NotForDeliveryReportException extends Exception {

	private static final long serialVersionUID = 8820354634366841014L;

	public NotForDeliveryReportException(String customer) {
		super("D/R can only be used for\n" + customer//
				+ "\nif no S/I has been used for a cancelled S/O");
	}
}
