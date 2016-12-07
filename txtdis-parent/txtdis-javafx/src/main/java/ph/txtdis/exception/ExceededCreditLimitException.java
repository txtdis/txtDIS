package ph.txtdis.exception;

import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;

import ph.txtdis.dto.Customer;

public class ExceededCreditLimitException extends Exception {

	private static final long serialVersionUID = -6656257234900338909L;

	public ExceededCreditLimitException(Customer customer, BigDecimal limit, BigDecimal excess) {
		super(customer + "\nhas exceeded its limit of " //
				+ toCurrencyText(limit) + " by " + toCurrencyText(excess));
	}
}
