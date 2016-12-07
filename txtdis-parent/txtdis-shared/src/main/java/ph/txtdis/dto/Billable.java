package ph.txtdis.dto;

import static ph.txtdis.type.BillableType.DELIVERY_REPORT;
import static ph.txtdis.type.BillableType.EX_TRUCK;
import static ph.txtdis.type.BillableType.INVOICE;
import static ph.txtdis.type.BillableType.SALES_ORDER;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.BillableType;
import ph.txtdis.util.NumberUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billable extends AbstractDecisionNeeded<Long>
		implements EntityDecisionNeeded<Long>, QuantifiedDetail, SalesforceEntity, Typed {

	private BigDecimal badOrderAllowanceValue, grossValue, totalValue, unpaidValue;

	private Boolean fullyPaid, isRma;

	private List<BillableDetail> details;

	private List<Long> discountIds;

	private List<String> discounts, payments;

	private LocalDate dueDate, orderDate;

	private Long bookingId, customerId, customerVendorId, numId, pickListId, receivingId;

	private String alias, billedBy, customerAddress, customerLocation, customerName, pickedBy, prefix, printedBy,
			receivedBy, receivingModifiedBy, route, suffix, truck, uploadedBy;

	private ZonedDateTime billedOn, pickedOn, printedOn, receivedOn, receivingModifiedOn, uploadedOn;

	@Override
	public String getIdNo() {
		return getOrderNo();
	}

	public String getOrderNo() {
		return prefix() + numId() + suffix();
	}

	private String prefix() {
		return prefix == null || prefix.isEmpty() ? "" : prefix + "-";
	}

	private String numId() {
		if (numId == null)
			return "";
		return isNegative(numId) ? "(" + -numId + ")" : numId.toString();
	}

	private String suffix() {
		return suffix == null ? "" : suffix;
	}

	public BigDecimal getGrossValue() {
		return grossValue == null ? BigDecimal.ZERO : grossValue;
	}

	public BigDecimal getTotalValue() {
		return totalValue == null ? BigDecimal.ZERO : totalValue;
	}

	@Override
	public String toString() {
		return (getOrderNo().isEmpty() ? "" : text() + " No. " + getOrderNo() + " - ") //
				+ customerName + ", " + orderDate + ", " + NumberUtils.toCurrencyText(totalValue);
	}

	private String text() {
		if (numId == null)
			return "S/O";
		return numId < 0 ? "D/R" : "S/I";
	}

	@Override
	public BillableType type() {
		if (numId != null && numId < 0)
			return DELIVERY_REPORT;
		if (billedOn != null)
			return INVOICE;
		if (customerName != null && customerName.startsWith("EX-TRUCK"))
			return EX_TRUCK;
		return SALES_ORDER;
	}
}
