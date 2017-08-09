package ph.txtdis.mgdc.ccbpi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCustomerDiscount;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDiscount //
	extends AbstractCustomerDiscount {

	@Override
	public String toString() {
		return "Discount=" + getDiscount() + ", start=" + getStartDate() + ", isValid=" + getIsValid();
	}
}
