package ph.txtdis.dto;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ph.txtdis.type.UomType;

public interface Fractioned {

	@JsonIgnore
	default Fraction getInitialQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getInitialQty().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getReturnedQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getReturnedQty().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getSoldQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getSoldQty().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default Fraction getFinalQtyInFractions() {
		return isNotPerCase() ? null : Fraction.getFraction(getFinalQty().intValue(), getQtyPerCase());
	}

	@JsonIgnore
	default boolean isNotPerCase() {
		return getQtyPerCase() == 0;
	}

	@JsonIgnore
	default BigDecimal getFinalQty() {
		return getInitialQty() //
				.subtract(getSoldQty()) //
				.subtract(getReturnedQty());
	}

	BigDecimal getInitialQty();

	BigDecimal getSoldQty();

	BigDecimal getReturnedQty();

	UomType getUom();

	int getQtyPerCase();
}