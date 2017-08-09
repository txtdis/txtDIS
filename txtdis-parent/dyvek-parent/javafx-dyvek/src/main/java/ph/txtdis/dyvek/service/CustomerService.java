package ph.txtdis.dyvek.service;

import java.util.List;

import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;
import ph.txtdis.type.PartnerType;

public interface CustomerService //
	extends MasterListedAndResetableService<Customer>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	UniqueNamedService<Customer> {

	List<String> listClients();

	Customer save(String name, PartnerType type) throws Exception;
}
