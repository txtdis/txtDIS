package ph.txtdis.exception;

import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;

public class BadCreditException extends Exception {

	private static final long serialVersionUID = -6656257234900338909L;

	public BadCreditException(String customer, BigDecimal overdue) {
		super(customer + "\nhas " + toCurrencyText(overdue) + " overdue");
	}
}
