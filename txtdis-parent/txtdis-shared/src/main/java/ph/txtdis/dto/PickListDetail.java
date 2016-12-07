package ph.txtdis.dto;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PickListDetail implements ItemVendorNo, Keyed<Long>, ReceivingDetail {

	private Long id;

	private String itemVendorNo, itemName;

	private BigDecimal pickedQty, returnedQty;

	private int qtyPerCase;

	@Override
	public BigDecimal getInitialQty() {
		return getPickedQty();
	}

	public BigDecimal getPickedQty() {
		return pickedQty == null ? BigDecimal.ZERO : pickedQty;
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
		return returnedQty == null ? BigDecimal.ZERO : returnedQty;
	}

	@JsonIgnore
	public Fraction getReturnedQtyInFractions() {
		return Fraction.getFraction(getReturnedQty().intValue(), qtyPerCase);
	}
}
