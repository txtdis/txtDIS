package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.txtdis.type.UomType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVolumeDiscount extends AbstractDecisionNeeded<Long>
		implements Keyed<Long>, EntityDecisionNeeded<Long>, Comparable<CustomerVolumeDiscount>, ItemStartDate {

	private Item item;

	private UomType uom;

	private BigDecimal targetQty, discountValue;

	private LocalDate startDate;

	@Override
	public int compareTo(CustomerVolumeDiscount vd) {
		return compareStartDates(this, vd);
	}
}
