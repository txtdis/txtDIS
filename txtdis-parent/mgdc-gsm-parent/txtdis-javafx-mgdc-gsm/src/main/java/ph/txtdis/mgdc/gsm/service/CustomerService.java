package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.FinancialService;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.MasterService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.type.PartnerType;

public interface CustomerService //
		extends FinancialService, ItemDeliveredCustomerService, ListedAndResetableService<Customer>, MasterService<Customer>,
		Qualified_CreditAndDiscountGivenCustomerService, ResettableService {

	boolean cannotReactivate();

	Customer findActive(Long id) throws Exception;

	Customer findEmployee(Long id) throws Exception;

	List<String> listNames();

	boolean noChangesNeedingApproval(String text);

	void reactivate() throws Information, Exception;

	void setType(PartnerType type);
}
