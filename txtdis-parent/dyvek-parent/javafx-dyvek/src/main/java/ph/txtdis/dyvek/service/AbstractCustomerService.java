package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

public abstract class AbstractCustomerService //
	implements CustomerService {

	@Autowired
	private RestClientService<Customer> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Override
	public RestClientService<Customer> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<Customer> getRestClientServiceForLists() {
		return restClientService;
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
		return restClientService.module(getModuleName()).save(c);
	}

	@Override
	public String getModuleName() {
		return "customer";
	}
}
