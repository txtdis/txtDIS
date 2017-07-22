package ph.txtdis.mgdc.gsm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCustomerDiscount;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDiscount //
		extends AbstractCustomerDiscount //
		implements ItemStartDate {

	private Item item;

	@Override
	public String toString() {
		return getItem() + ": discount=" + getDiscount() + ", start=" + getStartDate() + ", isValid=" + getIsValid();
	}
}
