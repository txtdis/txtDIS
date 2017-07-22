package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.FinancialService;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.MasterService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.type.PartnerType;

public interface CustomerService //
		extends FinancialService, ListedAndResetableService<Customer>, MasterService<Customer>, ResettableService {

	Customer findActive(Long id) throws Exception;

	void setType(PartnerType type);

	Route getRoute(Customer c, LocalDate d);
}
