package ph.txtdis.dto;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ModuleType;
import ph.txtdis.util.NumberUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesItemVariance //
		extends AbstractKeyed<Long> //
		implements Keyed<Long>, SellerSold, Typed {

	private String seller, orderNo, customer, item;

	private BigDecimal priceValue;

	private int otherCount, expectedCount, actualCount, returnedCount, qtyPerCase;

	public SalesItemVariance() {
		otherCount = 0;
		expectedCount = 0;
		actualCount = 0;
		returnedCount = 0;
	}

	public BigDecimal getActualQty() {
		return getQtyInCases(actualCount);
	}

	private BigDecimal getQtyInCases(int qtyInPieces) {
		return divide(qtyInPieces, qtyPerCase);
	}

	@JsonIgnore
	public Fraction getActualQtyInFractions() {
		return Fraction.getFraction(actualCount, qtyPerCase);
	}

	public BigDecimal getExpectedQty() {
		return getQtyInCases(expectedCount);
	}

	@JsonIgnore
	public Fraction getExpectedQtyInFractions() {
		return Fraction.getFraction(expectedCount, qtyPerCase);
	}

	public BigDecimal getExpectedValue() {
		return price().multiply(getExpectedQty());
	}

	public BigDecimal getOtherQty() {
		return getQtyInCases(otherCount);
	}

	@JsonIgnore
	public Fraction getOtherQtyInFractions() {
		return Fraction.getFraction(otherCount, qtyPerCase);
	}

	public BigDecimal getOtherValue() {
		return price().multiply(getOtherQty());
	}

	public BigDecimal getReturnedQty() {
		return getQtyInCases(returnedCount);
	}

	@JsonIgnore
	public Fraction getReturnedQtyInFractions() {
		return Fraction.getFraction(returnedCount, qtyPerCase);
	}

	public BigDecimal getReturnedValue() {
		return price().multiply(getReturnedQty());
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
		return expectedCount - actualCount - returnedCount;
	}

	@JsonIgnore
	public Fraction getVarianceQtyInFractions() {
		return Fraction.getFraction(getVariance(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getNonNegativeVarianceQtyInFractions() {
		return Fraction.getFraction(getNonNegativeVariance(), qtyPerCase);
	}

	private int getNonNegativeVariance() {
		return getVariance() < 0 ? 0 : getVariance();
	}

	@Override
	public String toString() {
		return item + ": o = " + getOtherQtyInFractions().toProperString() //
				+ ", e = " + getExpectedQtyInFractions().toProperString() //
				+ ", a = " + getActualQtyInFractions().toProperString() //
				+ ", r = " + getReturnedQtyInFractions().toProperString() //
				+ ", v = " + getVarianceQtyInFractions().toProperString() //
				+ " - " + NumberUtils.toCurrencyText(getValue());
	}

	@Override
	public ModuleType type() {
		return ModuleType.ORDER_CONFIRMATION;
	}
}
