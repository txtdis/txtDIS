package ph.txtdis.dto;

import lombok.Data;

import java.math.BigDecimal;

import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Data
public class AgingReceivable //
	implements Keyed<Long>,
	SellerSold {

	private Long id;

	private String seller, customer;

	private BigDecimal currentValue, oneToSevenValue, eightToFifteenValue, sixteenToThirtyValue, greaterThanThirtyValue,
		agingValue, totalValue;

	public BigDecimal getCurrentValue() {
		return zeroIfNull(currentValue);
	}

	public BigDecimal getOneToSevenValue() {
		return zeroIfNull(oneToSevenValue);
	}

	public BigDecimal getEightToFifteenValue() {
		return zeroIfNull(eightToFifteenValue);
	}

	public BigDecimal getSixteenToThirtyValue() {
		return zeroIfNull(sixteenToThirtyValue);
	}

	public BigDecimal getGreaterThanThirtyValue() {
		return zeroIfNull(greaterThanThirtyValue);
	}

	public BigDecimal getAgingValue() {
		return zeroIfNull(agingValue);
	}

	public BigDecimal getTotalValue() {
		return zeroIfNull(totalValue);
	}
}