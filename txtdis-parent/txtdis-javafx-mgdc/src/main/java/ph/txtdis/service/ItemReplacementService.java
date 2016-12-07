package ph.txtdis.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.BillableDetail;

public interface ItemReplacementService extends CustomerSearchableService, DecisionNeeded, Serviced<Long> {

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

	boolean isOffSite();

	boolean isReturnValid();

	void saveItemReturnReceiptData() throws Exception;

	void updateUponCustomerIdValidation(Long id);
}
