package ph.txtdis.dto;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import ph.txtdis.type.UomType;

@Data
public class PickListDetail //
		implements ItemVendorNo, Keyed<Long>, UnitMeasured, ReceivingDetail {

	private Long id;

	private String itemVendorNo, itemName;

	private BigDecimal pickedQty, returnedQty;

	private int qtyPerCase;

	@Override
	public BigDecimal getInitialQty() {
		return getPickedQty();
	}

	public BigDecimal getPickedQty() {
		return pickedQty == null ? ZERO : pickedQty;
	}

	public BigDecimal getPickedQtyInCases() {
		return divide(getPickedQty(), valueOf(qtyPerCase));
	}

	@JsonIgnore
	public Fraction getPickedQtyInFractions() {
		return Fraction.getFraction(getPickedQty().intValue(), qtyPerCase);
	}

	public BigDecimal getQty() {
		return getPickedQty().subtract(getReturnedQty());
	}

	@JsonIgnore
	public Fraction getQtyInFractions() {
		return Fraction.getFraction(getQty().intValue(), qtyPerCase);
	}

	@Override
	public BigDecimal getReturnedQty() {
		return returnedQty == null ? ZERO : returnedQty;
	}

	public BigDecimal getReturnedQtyInCases() {
		return divide(getReturnedQty(), valueOf(qtyPerCase));
	}

	@JsonIgnore
	public Fraction getReturnedQtyInFractions() {
		return Fraction.getFraction(getReturnedQty().intValue(), qtyPerCase);
	}

	@Override
	@JsonIgnore
	public UomType getUom() {
		return UomType.CS;
	}
}
