package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.CreationLogged;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.service.AppendableDetailService;
import ph.txtdis.service.CustomerIdAndNameService;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service.ItemInputtedService;
import ph.txtdis.service.OpenDialogHeaderTextService;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.service.QuantityValidated;
import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

public interface BillableService //
		extends AppendableDetailService, CreationLogged, CustomerIdAndNameService, DecisionNeededService, ItemBasedService<BillableDetail>,
		ItemInputtedService<BillableDetail>, OpenDialogHeaderTextService, QtyPerUomService, QuantityValidated,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	String getBilledBy();

	ZonedDateTime getBilledOn();

	Long getBookingId();

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

	void setBookingId(Long id);

	void setDetails(List<BillableDetail> d);

	void setItem(Item item);

	void setOrderDate(LocalDate value);

	@Override
	void setRemarks(String remarks);

	void setUnpaidValue(BigDecimal unpaid);

	LocalDate today();

	void updateSummaries(List<BillableDetail> items);

}
