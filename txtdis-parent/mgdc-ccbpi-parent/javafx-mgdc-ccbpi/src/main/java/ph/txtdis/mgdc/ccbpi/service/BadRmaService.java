package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.CustomerSearchableService;
import ph.txtdis.service.DecisionNeededService;

public interface BadRmaService //
		extends BillableService, CustomerSearchableService, DecisionNeededService {

	boolean isReturnValid();

	void saveReturnReceiptData() throws Information, Exception;

	void setCustomerData(Customer c);

	void updateUponCustomerIdValidation(Long id) throws Exception;
}