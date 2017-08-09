package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.CreationLogged;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface BillableService //
	extends AppendableDetailService,
	CreationLogged,
	CustomerIdAndNameService,
	DecisionNeededService,
	ItemBasedService<BillableDetail>,
	ItemInputtedService<BillableDetail>,
	OpenDialogHeaderTextService,
	QtyPerUomService,
	QuantityValidated,
	RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	String getBilledBy();

	ZonedDateTime getBilledOn();

	Long getBookingId();

	void setBookingId(Long id);

	String getCustomerAddress();

	String getDateLabelName();

	List<String> getDiscounts();

	LocalDate getDueDate();

	BigDecimal getGrossValue();

	default String getIdPrompt() {
		return "S/I(D/R) No.";
	}

	@Override
	default String getItemName() {
		return getItem() == null ? null : getItem().getDescription();
	}

	LocalDate getOrderDate();

	void setOrderDate(LocalDate value);

	List<String> getPayments();

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

	BigDecimal getTotalValue();

	void invalidate();

	boolean isAuditor();

	void setDetails(List<BillableDetail> d);

	void setItem(Item item);

	void setUnpaidValue(BigDecimal unpaid);

	LocalDate today();

	void updateSummaries(List<BillableDetail> items);

}
