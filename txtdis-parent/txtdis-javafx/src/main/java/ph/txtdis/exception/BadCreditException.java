package ph.txtdis.exception;

import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;

import ph.txtdis.dto.Customer;

public class BadCreditException extends Exception {

	private static final long serialVersionUID = -6656257234900338909L;

	public BadCreditException(Customer customer, BigDecimal overdue) {
		super(customer + "\nhas " + toCurrencyText(overdue) + " overdue");
	}
}
