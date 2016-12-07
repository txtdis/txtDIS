package ph.txtdis.dto;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.util.NumberUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesItemVariance extends AbstractId<Long> implements Keyed<Long>, SellerSold {

	private String seller, item;

	private BigDecimal priceValue;

	private int expected, actual, qtyPerCase;

	public BigDecimal getActualQty() {
		return getQtyInCases(actual);
	}

	private BigDecimal getQtyInCases(int qtyInPieces) {
		BigDecimal qty = new BigDecimal(qtyInPieces);
		return qty.divide(new BigDecimal(qtyPerCase), 4, RoundingMode.HALF_EVEN);
	}

	@JsonIgnore
	public Fraction getActualQtyInFractions() {
		return Fraction.getFraction(actual, qtyPerCase);
	}

	public BigDecimal getExpectedQty() {
		return getQtyInCases(expected);
	}

	@JsonIgnore
	public Fraction getExpectedQtyInFractions() {
		return Fraction.getFraction(expected, qtyPerCase);
	}

	public BigDecimal getValue() {
		return price().multiply(getVarianceQty());
	}

	private BigDecimal price() {
		return priceValue == null ? ZERO : priceValue;
	}

	public BigDecimal getVarianceQty() {
		return getQtyInCases(getVariance());
	}

	private int getVariance() {
		return expected - actual;
	}

	@JsonIgnore
	public Fraction getVarianceQtyInFractions() {
		return Fraction.getFraction(getVariance(), qtyPerCase);
	}

	@Override
	public String toString() {
		return item + ": " + NumberUtils.toCurrencyText(getValue());
	}
}
