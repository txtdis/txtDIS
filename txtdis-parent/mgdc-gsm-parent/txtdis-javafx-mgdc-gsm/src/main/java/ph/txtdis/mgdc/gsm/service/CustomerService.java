package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.FinancialService;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.MasterService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.type.PartnerType;

import java.util.List;

public interface CustomerService //
	extends FinancialService,
	ItemDeliveredCustomerService,
	ListedAndResettableService<Customer>,
	MasterService<Customer>,
	QualifiedCreditAndDiscountGivenCustomerService,
	ResettableService {

	boolean cannotReactivate();

	Customer findActive(Long id) throws Exception;

	Customer findEmployee(Long id) throws Exception;

	List<String> listNames();

	boolean noChangesNeedingApproval(String text);

	void reactivate() throws Information, Exception;

	void setType(PartnerType type);
}
