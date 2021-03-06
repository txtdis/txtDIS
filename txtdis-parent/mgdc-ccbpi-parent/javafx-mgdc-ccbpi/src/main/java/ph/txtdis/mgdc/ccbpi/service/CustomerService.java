package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.FinancialService;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.MasterService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.type.PartnerType;

import java.time.LocalDate;

public interface CustomerService //
	extends FinancialService,
	ListedAndResettableService<Customer>,
	MasterService<Customer>,
	ResettableService {

	Customer findActive(Long id) throws Exception;

	void setType(PartnerType type);

	Route getRoute(Customer c, LocalDate d);
}
