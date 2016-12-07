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

	private BigDecimal initialQty, soldQty, returnedQty, freeQty, priceValue, discountValue;

	private QualityType quality;

	private Integer onPurchaseDaysLevel, onReceiptDaysLevel;

	private int unitQty, qtyPerCase;

	public String getItemQuality() {
		return quality + ":" + itemName;
	}

	public BigDecimal getDiscountedSubtotalValue() {
		return (price().subtract(discount())).multiply(qty());
	}

	private BigDecimal qty() {
		BigDecimal qty = getQty();
		if (qtyPerCase != 0)
			qty = qty.divide(new BigDecimal(qtyPerCase), 4, RoundingMode.HALF_EVEN);
		return qty;
	}

	private BigDecimal discount() {
		BigDecimal d = getDiscountValue();
		return d != null ? d : ZERO;
	}

	public BigDecimal getSubtotalValue() {
		return price().multiply(qty());
	}

	private BigDecimal price() {
		return priceValue == null ? ZERO : priceValue;
	}

	@Override
	public String toString() {
		return itemName + ": " + NumberUtils.toCurrencyText(getSubtotalValue());
	}
}
