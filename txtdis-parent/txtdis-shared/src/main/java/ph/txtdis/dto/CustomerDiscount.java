package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UomType;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDiscount extends AbstractDecisionNeeded<Long>
		implements Comparable<CustomerDiscount>, EntityDecisionNeeded<Long>, ItemStartDate {

	private int level;

	private BigDecimal discount;

	private Item item;

	private ItemFamily familyLimit;

	private LocalDate startDate;

	private UomType uom;

	@Override
	public int compareTo(CustomerDiscount cd) {
		return compareStartDates(this, cd);
	}
}
