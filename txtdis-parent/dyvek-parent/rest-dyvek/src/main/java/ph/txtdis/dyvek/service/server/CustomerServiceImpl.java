package ph.txtdis.dyvek.service.server;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.PartnerType.FINANCIAL;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PartnerType.VENDOR;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.repository.CustomerRepository;
import ph.txtdis.service.AbstractCreateNameListService;

@Service("customerService")
public class CustomerServiceImpl //
		extends AbstractCreateNameListService<CustomerRepository, CustomerEntity, Customer> //
		implements CustomerService {

	@Override
	public List<Customer> findAllBanks() {
		List<CustomerEntity> l = repository.findByType(FINANCIAL);
		return toModels(l);
	}

	@Override
	public List<Customer> findAllTradingClients() {
		List<CustomerEntity> l = repository.findByType(OUTLET);
		return toModels(l);
	}

	@Override
	public List<Customer> findAllTradingPartners() {
		List<CustomerEntity> l = repository.findByTypeIn(asList(VENDOR, OUTLET));
		return toModels(l);
	}

	@Override
	public List<Customer> findAllTruckingClients() {
		List<CustomerEntity> l = repository.findByType(EX_TRUCK);
		return toModels(l);
	}

	@Override
	public List<Customer> findAllVendors() {
		List<CustomerEntity> l = repository.findByType(VENDOR);
		return toModels(l);
	}

	@Override
	public Customer findByPrimaryKey(Long id) {
		CustomerEntity e = repository.findOne(id);
		return toModel(e);
	}

	@Override
	public CustomerEntity findEntityByName(String name) {
		return super.findEntityByName(name);
	}

	@Override
	protected Customer toModel(CustomerEntity e) {
		if (e == null)
			return null;
		Customer c = new Customer();
		c.setId(e.getId());
		c.setName(e.getName());
		c.setType(e.getType());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		return c;
	}

	@Override
	protected CustomerEntity toEntity(Customer c) {
		if (c == null)
			return null;
		CustomerEntity e = new CustomerEntity();
		e.setName(c.getName());
		e.setType(c.getType());
		return e;
	}
}