package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.util.NumberUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Price extends AbstractDecisionNeeded<Long>
		implements Keyed<Long>, EntityDecisionNeeded<Long>, Comparable<Price>, StartDated {

	private PricingType type;

	private BigDecimal priceValue;

	private Channel channelLimit;

	private LocalDate startDate;

	@Override
	public int compareTo(Price p) {
		return compareStartDates(this, p);
	}

	@Override
	public String toString() {
		return type + " Price : " + NumberUtils.toCurrencyText(priceValue) //
				+ " limited to " + (channelLimit == null ? "ALL" : channelLimit)//
				+ " starting " + startDate;
	}
}
