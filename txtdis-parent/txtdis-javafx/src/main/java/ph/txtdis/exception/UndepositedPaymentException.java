package ph.txtdis.exception;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

import ph.txtdis.type.PaymentType;

public class UndepositedPaymentException
	extends Exception {

	private static final long serialVersionUID = 4671867557387899786L;

	public UndepositedPaymentException(PaymentType t, Long id) {
		super("No booking if " + lowerCase(t.toString()) + " payments\n"//
			+ "have NOT been deposited or fund-transferred;\n"//
			+ capitalizeFully(t.toString()) + " Collection No. " + id + " is still not");
	}
}
