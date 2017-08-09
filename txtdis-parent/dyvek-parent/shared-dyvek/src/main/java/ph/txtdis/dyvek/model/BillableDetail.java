package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractKeyed;
import ph.txtdis.dto.Keyed;

import java.math.BigDecimal;
import java.time.LocalDate;

import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillableDetail
	extends AbstractKeyed<Long>
	implements Keyed<Long> {

	private LocalDate orderDate;

	private String orderNo, customer, item;

	private BigDecimal assignedQty, priceValue, qty;

	public BigDecimal getAssignedQty() {
		return zeroIfNull(assignedQty);
	}

	public BigDecimal getValue() {
		return getPriceValue().multiply(getQty());
	}

	public BigDecimal getPriceValue() {
		return zeroIfNull(priceValue);
	}

	public BigDecimal getQty() {
		return zeroIfNull(qty);
	}
}
