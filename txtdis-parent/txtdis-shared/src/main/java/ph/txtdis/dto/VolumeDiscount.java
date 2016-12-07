package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.txtdis.type.UomType;
import ph.txtdis.type.VolumeDiscountType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VolumeDiscount extends AbstractDecisionNeeded<Long>
		implements Keyed<Long>, EntityDecisionNeeded<Long>, Comparable<VolumeDiscount>, StartDated {

	private VolumeDiscountType type;

	private UomType uom;

	private int cutoff;

	private BigDecimal discount;

	private Channel channelLimit;

	private LocalDate startDate;

	@Override
	public int compareTo(VolumeDiscount vd) {
		return compareStartDates(this, vd);
	}
}
