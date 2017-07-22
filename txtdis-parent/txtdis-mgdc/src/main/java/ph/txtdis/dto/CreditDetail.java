package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditDetail //
		extends AbstractDecisionNeeded<Long> //
		implements CustomerCredit {

	private int termInDays, gracePeriodInDays;

	private BigDecimal creditLimit;

	private LocalDate startDate;

	@Override
	public int compareTo(CustomerCredit o) {
		return compareStartDates(this, o);
	}
}
