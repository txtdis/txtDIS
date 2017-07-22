package ph.txtdis.dyvek.model;

import static ph.txtdis.type.StatusType.CLOSED;
import static ph.txtdis.type.StatusType.OPEN;
import static ph.txtdis.util.ListUtils.emptyIfNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.type.StatusType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billable //
		extends AbstractCreationTracked<Long> {

	private Long checkId;

	private BigDecimal totalQty, balanceQty, //
			priceValue, adjustmentQty, adjustmentPriceValue, totalValue, //
			iodineQty, lauricPercent, oleicPercent, moisturePercent, saponificationPercent, tolerancePercent;

	private List<BillableDetail> deliveries, purchases, bookings, billings;

	private LocalDate endDate, orderDate, billDate, checkDate;

	private String vendor, client, item, itemDescription, purchaseNo, deliveryNo, salesNo, truckPlateNo, truckScaleNo, color, remarks, billNo,
			billActedBy, closedBy, bank, paymentActedBy;

	private ZonedDateTime billActedOn, closedOn, paymentActedOn;

	public StatusType getStatus() {
		return closedOn == null ? OPEN : CLOSED;
	}

	public List<BillableDetail> getDeliveries() {
		return emptyIfNull(deliveries);
	}

	public List<BillableDetail> getPurchases() {
		return emptyIfNull(purchases);
	}

	public List<BillableDetail> getBookings() {
		return emptyIfNull(bookings);
	}

	public List<BillableDetail> getBillings() {
		return emptyIfNull(billings);
	}
}
