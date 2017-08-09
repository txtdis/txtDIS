package ph.txtdis.mgdc.service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.CustomerSearchableService;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service
	.SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface ItemReplacementService
	extends CustomerSearchableService,
	DecisionNeededService,
	SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	Long getBookingId();

	String getCustomerAddress();

	List<BillableDetail> getDetails();

	LocalDate getOrderDate();

	String getReceivedBy();

	ZonedDateTime getReceivedOn();

	Long getReceivingId();

	String getReplacedBy();

	ZonedDateTime getReplacedOn();

	void invalidate();

	boolean isReturnValid();

	void saveItemReturnReceiptData() throws Exception;

	void updateUponCustomerIdValidation(Long id);
}
