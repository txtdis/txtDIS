package ph.txtdis.dto;

import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billable extends EntityDecisionNeeded<Long> implements Remarked<Long>, SalesforceEntity {

	private BigDecimal badOrderAllowanceValue, unpaidValue, totalValue, grossValue;

	private Boolean isRma, fullyPaid;

	private List<BillableDetail> details;

	private List<Long> discountIds;

	private List<String> discounts, payments;

	private LocalDate dueDate, orderDate;

	private Long numId, bookingId, customerId, receivingId;

	private String prefix, suffix, customerName, customerAddress, customerLocation, route, billedBy, printedBy,
			receivedBy, receivingModifiedBy, truck, uploadedBy;

	private ZonedDateTime billedOn, printedOn, receivedOn, receivingModifiedOn, uploadedOn;

	public String getOrderNo() {
		return prefix() + numId() + suffix();
	}

	@Override
	public String toString() {
		return text() + " No. " + getOrderNo();
	}

	private Long numId() {
		if (numId == null)
			return 0L;
		return isNegative(numId) ? -numId : numId;
	}

	private String prefix() {
		return prefix == null || prefix.isEmpty() ? "" : prefix + "-";
	}

	private String suffix() {
		return suffix == null ? "" : suffix;
	}

	private String text() {
		if (numId == null)
			return "S/O";
		return numId < 0 ? "D/R" : "S/I";
	}

	@Override
	public String getIdNo() {
		return getOrderNo();
	}
}
