package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.type.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.type.StatusType.CLOSED;
import static ph.txtdis.type.StatusType.OPEN;
import static ph.txtdis.util.ListUtils.emptyIfNull;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billable
	extends AbstractCreationTracked<Long> {

	private Long checkId, cashAdvanceId;

	private BigDecimal totalQty, balanceQty, priceValue, adjustmentQty, adjustmentPriceValue, totalValue,
		ffaPercent, iodineQty, tolerancePercent, paymentValue, cashAdvanceValue, grossWeight;

	private List<BillableDetail> deliveries, purchases, bookings, billings;

	private LocalDate endDate, orderDate, billDate, checkDate, cashAdvanceDate;

	private String vendor, client, item, itemDescription, purchaseNo, deliveryNo, salesNo, truckPlateNo, truckScaleNo,
		color, remarks, billNo, billActedBy, closedBy, bank, paymentActedBy;

	private ZonedDateTime billActedOn, closedOn, paymentActedOn;

	public List<BillableDetail> getBookings() {
		return emptyIfNull(bookings);
	}

	public List<BillableDetail> getBillings() {
		return emptyIfNull(billings);
	}

	public List<BillableDetail> getDeliveries() {
		return emptyIfNull(deliveries);
	}

	public List<BillableDetail> getPurchases() {
		return emptyIfNull(purchases);
	}

	public StatusType getStatus() {
		return closedOn == null ? OPEN : CLOSED;
	}

	public BigDecimal getTareWeight() {
		return isZero(getGrossWeight()) ? ZERO : getGrossWeight().subtract(getTotalQty());
	}

	public BigDecimal getGrossWeight() {
		return zeroIfNull(grossWeight);
	}

	public BigDecimal getTotalQty() {
		return zeroIfNull(totalQty);
	}
}
