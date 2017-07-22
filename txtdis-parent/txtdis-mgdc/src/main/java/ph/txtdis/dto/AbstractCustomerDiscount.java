package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractCustomerDiscount //
		extends AbstractDecisionNeeded<Long> //
		implements ForApproval, StartDated {

	private BigDecimal discount;

	private LocalDate startDate;

	@Override
	public String toString() {
		return "Discount=" + getDiscount() + ", start=" + getStartDate() + ", isValid=" + getIsValid();
	}
}
