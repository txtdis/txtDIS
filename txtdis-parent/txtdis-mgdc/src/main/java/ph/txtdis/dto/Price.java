package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.util.NumberUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class Price //
		extends AbstractDecisionNeeded<Long> //
		implements ForApproval, StartDated {

	private PricingType type;

	private BigDecimal priceValue;

	private LocalDate startDate;

	@Override
	public String toString() {
		return type + " Price : " + NumberUtils.toCurrencyText(priceValue) //
				+ " starting " + startDate;
	}
}
