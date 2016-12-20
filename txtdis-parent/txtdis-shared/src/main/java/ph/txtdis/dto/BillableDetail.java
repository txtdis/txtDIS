package ph.txtdis.dto;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.NumberUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillableDetail extends AbstractId<Long>
		implements ItemVendorNo, Keyed<Long>, Fractioned, ReceivingDetail, UnitMeasured {

	private String itemName, itemVendorNo;

	private UomType uom;

	private BigDecimal initialQty, soldQty, returnedQty, freeQty, priceValue;

	private QualityType quality;

	private Integer onPurchaseDaysLevel, onReceiptDaysLevel;

	private int unitQty, qtyPerCase;

	public String getItemQuality() {
		return quality + ":" + itemName;
	}

	private BigDecimal billableQty(BigDecimal unitQty) {
		if (qtyPerCase != 0)
			unitQty = unitQty.divide(new BigDecimal(qtyPerCase), 4, RoundingMode.HALF_EVEN);
		return unitQty;
	}

	public BigDecimal getFinalSubtotalValue() {
		return subtotal(getFinalQty());
	}

	private BigDecimal subtotal(BigDecimal unitQty) {
		return price().multiply(billableQty(unitQty));
	}

	private BigDecimal price() {
		return priceValue == null ? ZERO : priceValue;
	}

	public BigDecimal getInitialSubtotalValue() {
		return subtotal(getInitialQty());
	}

	@Override
	public String toString() {
		return itemName + ": " + NumberUtils.toCurrencyText(getFinalSubtotalValue());
	}
}
