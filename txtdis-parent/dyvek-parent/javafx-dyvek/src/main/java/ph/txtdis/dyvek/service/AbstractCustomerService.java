package ph.txtdis.dyvek.service;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractCustomerService //
		implements CustomerService {

	@Autowired
	protected ReadOnlyService<Customer> readOnlyService;

	@Autowired
	private SavingService<Customer> savingService;

	@Autowired
	private ClientTypeMap typeMap;

	@Override
	public ReadOnlyService<Customer> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "customer";
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<Customer> list() {
		return list(customerType());
	}

	private String customerType() {
		return uncapitalize(getHeaderName().replace(" ", "")) + "s";
	}

	@Override
	public List<String> listClients() {
		return listNames(customerType());
	}

	@Override
	public void reset() {
	}

	@Override
	public Customer save(String name, PartnerType type) throws Exception {
		Customer c = new Customer();
		c.setName(name);
		c.setType(type);
		return savingService.module(getModuleName()).save(c);
	}
}
