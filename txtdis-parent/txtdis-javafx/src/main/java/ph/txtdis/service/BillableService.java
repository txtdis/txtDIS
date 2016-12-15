package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.Item;

public interface BillableService
		extends CreationTracked, CustomerIdAndNameService, Detailed, ItemBasedService<BillableDetail>,
		ItemInputtedService<BillableDetail>, QtyPerUomService, QuantityValidated, Reset, Serviced<Long> {

	BigDecimal getBadOrderAllowanceValue();

	String getBilledBy();

	ZonedDateTime getBilledOn();

	Long getBookingId();

	String getCustomerAddress();

	String getDateLabelName();

	String getDecidedBy();

	ZonedDateTime getDecidedOn();

	List<String> getDiscounts();

	LocalDate getDueDate();

	BigDecimal getGrossValue();

	default String getIdPrompt() {
		return "S/I(D/R) No.";
	}

	Boolean getIsValid();

	@Override
	default String getItemName() {
		return getItem() == null ? null : getItem().getDescription();
	}

	Long getNumId();

	LocalDate getOrderDate();

	List<String> getPayments();

	String getPrefix();

	String getPrintedBy();

	ZonedDateTime getPrintedOn();

	String getReceivedBy();

	ZonedDateTime getReceivedOn();

	Long getReceivingId();

	String getReceivingModifiedBy();

	ZonedDateTime getReceivingModifiedOn();

	default String getReceivingPrompt() {
		return "R/R No.";
	}

	default String getReferencePrompt() {
		return "S/O No.";
	}

	String getRemarks();

	String getSuffix();

	BigDecimal getTotalValue();

	void invalidate();

	@Override
	default boolean isNew() {
		return getId() == null;
	}

	boolean isOffSite();

	List<Booking> listUnpicked(LocalDate pickDate);

	void setDetails(List<BillableDetail> d);

	void setItem(Item item);

	void setOrderDate(LocalDate value);

	void setRemarks(String remarks);

	void setUnpaidValue(BigDecimal unpaid);
}
