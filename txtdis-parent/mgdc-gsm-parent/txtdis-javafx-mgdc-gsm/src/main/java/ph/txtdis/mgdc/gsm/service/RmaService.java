package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.CustomerSearchableService;
import ph.txtdis.service.DecisionNeededService;

public interface RmaService //
	extends BillableService,
	CustomerSearchableService,
	DecisionNeededService {

	@Override
	@SuppressWarnings("unchecked")
	Billable get();

	boolean isReturnValid();

	void saveReturnReceiptData() throws Information, Exception;

	void setCustomerData(Customer c);

	void updateUponCustomerIdValidation(Long id) throws Exception;
}
