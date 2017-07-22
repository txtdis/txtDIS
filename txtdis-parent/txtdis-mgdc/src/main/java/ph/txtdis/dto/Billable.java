package ph.txtdis.dto;

import static java.util.Collections.emptyList;
import static ph.txtdis.type.ModuleType.DELIVERY_REPORT;
import static ph.txtdis.type.ModuleType.EX_TRUCK;
import static ph.txtdis.type.ModuleType.INVOICE;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.PartnerType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billable //
		extends AbstractDecisionNeeded<Long> //
		implements Booked, DecisionNeededValidatedCreatedKeyed<Long>, FractionedDetailed, Typed {

	private boolean canChangeDetails, detailsChanged, isLoaded;

	private BigDecimal grossValue, adjustmentValue, totalValue, unpaidValue;

	private Boolean fullyPaid, isRma;

	private List<BillableDetail> details;

	private List<Long> discountIds;

	private List<String> discounts, payments;

	private LocalDate dueDate, orderDate;

	private Long bookingId, customerId, customerVendorId, employeeId, numId, pickListId, receivingId;

	private String alias, billedBy, customerAddress, customerLocation, customerName, driver, helper, pickedBy, prefix, printedBy, receivedBy,
			receivingModifiedBy, route, seller, suffix, truck;

	private ZonedDateTime billedOn, pickedOn, printedOn, receivedOn, receivingModifiedOn;

	public BigDecimal getAdjustmentValue() {
		return zeroIfNull(adjustmentValue);
	}

	@Override
	public List<BillableDetail> getDetails() {
		return details == null ? emptyList() : details;
	}

	public BigDecimal getFinalQty() {
		return zeroIfNull(detail().getFinalQty());
	}

	public BigDecimal getGrossValue() {
		return zeroIfNull(grossValue);
	}

	public String getItemName() {
		return detail().getItemName();
	}

	private BillableDetail detail() {
		try {
			return getDetails().get(0);
		} catch (Exception e) {
			return new BillableDetail();
		}
	}

	public String getOrderNo() {
		return prefix() + numId() + suffix();
	}

	public BigDecimal getPriceValue() {
		return zeroIfNull(detail().getPriceValue());
	}

	private String prefix() {
		return prefix == null ? "" : prefix + hyphen();
	}

	private String hyphen() {
		return numId == null ? "" : "-";
	}

	private String numId() {
		if (numId == null)
			return "";
		return isNegative(numId) ? "(" + -numId + ")" : numId.toString();
	}

	private String suffix() {
		return suffix == null ? "" : suffix;
	}

	public BigDecimal getTotalValue() {
		return zeroIfNull(totalValue);
	}

	@Override
	public String toString() {
		return getOrderNo();
	}

	@Override
	public ModuleType type() {
		if (numId != null) {
			if (numId < 0)
				return DELIVERY_REPORT;
			else
				return INVOICE;
		} else if (customerName != null && customerName.startsWith(PartnerType.EX_TRUCK.toString()))
			return EX_TRUCK;
		return SALES_ORDER;
	}
}
