package ph.txtdis.dto;

import static ph.txtdis.util.NumberUtils.toCurrencyText;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillableDetail //
		extends AbstractKeyed<Long> //
		implements ItemVendorNo, Fractioned, ReceivingDetail, UnitMeasured {

	private String itemName, itemVendorNo;

	private UomType uom;

	private BigDecimal initialQty, soldQty, returnedQty, priceValue;

	private QualityType quality;

	private int unitQty, qtyPerCase;

	private boolean isInvalid;

	public BigDecimal getFinalQtyInCases() {
		return qtyInCases(getFinalQty());
	}

	public BigDecimal getFinalSubtotalValue() {
		return subtotal(getFinalQty());
	}

	private BigDecimal subtotal(BigDecimal unitQty) {
		return getPriceValue().multiply(qtyInCases(unitQty));
	}

	private BigDecimal qtyInCases(BigDecimal unitQty) {
		if (qtyPerCase != 0)
			unitQty = unitQty.divide(BigDecimal.valueOf(qtyPerCase), 4, RoundingMode.HALF_EVEN);
		return unitQty;
	}

	@Override
	public BigDecimal getInitialQty() {
		return zeroIfNull(initialQty);
	}

	public BigDecimal getInitialQtyInCases() {
		return qtyInCases(getInitialQty());
	}

	public BigDecimal getInitialSubtotalValue() {
		return subtotal(getInitialQty());
	}

	public String getItemQuality() {
		return quality + ":" + itemName;
	}

	public BigDecimal getPriceValue() {
		return zeroIfNull(priceValue);
	}

	@Override
	public BigDecimal getReturnedQty() {
		return zeroIfNull(returnedQty);
	}

	public BigDecimal getReturnedQtyInCases() {
		return qtyInCases(getReturnedQty());
	}

	public BigDecimal getReturnedSubtotalValue() {
		return subtotal(getReturnedQty());
	}

	@Override
	public BigDecimal getSoldQty() {
		return zeroIfNull(soldQty);
	}

	@Override
	public String toString() {
		return "\n" + itemName //
				+ " -i=" + initialQty //
				+ " -r=" + returnedQty //
				+ " -s=" + soldQty //
				+ toCurrencyText(priceValue);
	}
}
