package ph.txtdis.service;

import java.time.ZonedDateTime;

public interface RmaReplacementService extends BillableService, CustomerSearchableService, DecisionNeeded {

	String getReplacedBy();

	ZonedDateTime getReplacedOn();

	Long getReplacementId();

	boolean isReturnValid();

	void saveItemReturnReceiptData() throws Exception;

	void updateUponCustomerIdValidation(Long id) throws Exception;
}
