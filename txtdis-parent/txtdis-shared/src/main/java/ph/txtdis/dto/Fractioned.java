package ph.txtdis.dto;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ph.txtdis.type.UomType;

public interface Fractioned {

	@JsonIgnore
	default Fraction getInitialQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getInitialQtyInDecimals().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getReturnedQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getReturnedQtyInDecimals().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getSoldQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getSoldQtyInDecimals().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getFreeQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getFreeQtyInDecimals().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getFinalQty().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default boolean isNotPerCase() {
		return getQtyPerCase() == 0;
	}

	@JsonIgnore
	default BigDecimal getInitialQtyInDecimals() {
		return getInitialQty() == null ? ZERO : getInitialQty();
	}

	@JsonIgnore
	default BigDecimal getReturnedQtyInDecimals() {
		return getReturnedQty() == null ? ZERO : getReturnedQty();
	}

	@JsonIgnore
	default BigDecimal getSoldQtyInDecimals() {
		return getSoldQty() == null ? ZERO : getSoldQty();
	}

	@JsonIgnore
	default BigDecimal getFreeQtyInDecimals() {
		return getFreeQty() == null ? ZERO : getFreeQty();
	}

	@JsonIgnore
	default BigDecimal getFinalQtyInDecimals() {
		return getInitialQtyInDecimals() //
				.subtract(getSoldQtyInDecimals()) //
				.subtract(getReturnedQtyInDecimals()) //
				.subtract(getFreeQtyInDecimals());
	}

	default BigDecimal getFinalQty() {
		BigDecimal d = getFinalQtyInDecimals();
		return isNegative(d) ? ZERO : d;
	}

	BigDecimal getInitialQty();

	BigDecimal getSoldQty();

	BigDecimal getFreeQty();

	BigDecimal getReturnedQty();

	UomType getUom();

	int getQtyPerCase();
}