package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static ph.txtdis.type.PartnerType.FINANCIAL;
import static ph.txtdis.type.PartnerType.OUTLET;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.ccbpi.repository.CustomerRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PartnerType;

@Service("customerService")
public class CustomerServiceImpl //
	extends AbstractSpunSavedKeyedService<CustomerRepository, CustomerEntity, Customer, Long> //
	implements CustomerService {

	@Override
	public Customer findByPrimaryKey(Long id) {
		CustomerEntity e = repository.findOne(id);
		return toModel(e);
	}

	@Override
	protected Customer toModel(CustomerEntity e) {
		return e == null ? null : newCustomer(e);
	}

	private Customer newCustomer(CustomerEntity e) {
		Customer c = basicInfoOnly(e);
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		c.setDeactivatedBy(e.getDeactivatedBy());
		c.setDeactivatedOn(e.getDeactivatedOn());
		c.setLastModifiedBy(e.getLastModifiedBy());
		c.setLastModifiedOn(e.getLastModifiedOn());
		c.setType(e.getType());
		return c;
	}

	private Customer basicInfoOnly(CustomerEntity e) {
		return idAndNameOnly(e);
	}

	private Customer idAndNameOnly(CustomerEntity e) {
		if (e == null)
			return null;
		Customer c = new Customer();
		c.setId(e.getId());
		c.setName(e.getName());
		return c;
	}

	@Override
	public List<Customer> findAll() {
		Iterable<CustomerEntity> l = repository.findAll();
		return toBasicInfoOnlyList(l);
	}

	private List<Customer> toBasicInfoOnlyList(Iterable<CustomerEntity> l) {
		return l == null ? null //
			: stream(l.spliterator(), false).map(c -> basicInfoOnly(c)).collect(Collectors.toList());
	}

	@Override
	public List<Customer> findBanks() {
		return findAllByType(FINANCIAL);
	}

	private List<Customer> findAllByType(PartnerType type) {
		List<CustomerEntity> l = findEntitiesByType(type);
		return l.stream().map(c -> idAndNameOnly(c)).collect(Collectors.toList());
	}

	private List<CustomerEntity> findEntitiesByType(PartnerType type) {
		return repository.findByDeactivatedOnNullAndTypeOrderByNameAsc(type);
	}

	@Override
	public Customer findByName(String name) {
		return toModel(findEntityByName(name));
	}

	@Override
	public CustomerEntity findEntityByName(String name) {
		return repository.findByNameIgnoreCase(name);
	}

	@Override
	public Customer findByVendorId(Long id) {
		CustomerEntity e = repository.findByVendorId(id);
		return toModel(e);
	}

	@Override
	public CustomerEntity findEntity(Billable b) {
		Long id = b.getCustomerId();
		return id == null ? findEntityByName(b.getCustomerName()) : findEntityByPrimaryKey(id);
	}

	@Override
	public List<Customer> listOutletIdsAndNames() {
		return findAllByType(OUTLET);
	}

	@Override
	public List<Customer> listOutlets() {
		List<CustomerEntity> l = findEntitiesByType(OUTLET);
		return l == null ? null
			: l.stream().map(e -> toModel(e)) //
			.sorted(Comparator.comparing(Customer::getId)) //
			.collect(toList());
	}

	@Override
	public List<Customer> searchByName(String name) {
		List<CustomerEntity> l = repository.findByNameContaining(name);
		return l == null ? null : l.stream().map(c -> basicInfoOnly(c)).collect(Collectors.toList());
	}

	@Override
	public Customer toBank(String id) {
		return findByKey(id);
	}

	private Customer findByKey(String key) {
		return toModel(findEntityByKey(key));
	}

	private CustomerEntity findEntityByKey(String key) {
		return repository.findOne(Long.valueOf(key));
	}

	@Override
	public CustomerEntity toBankEntity(String id) {
		return findEntityByKey(id);
	}

	@Override
	public CustomerEntity toEntity(Customer c) {
		return c == null ? null : entity(c);
	}

	private CustomerEntity entity(Customer c) {
		CustomerEntity e = findEntity(c);
		if (e == null)
			e = new CustomerEntity();
		if (e.getDeactivatedBy() == null && c.getDeactivatedBy() != null)
			return deactivate(e, c.getDeactivatedBy());
		e.setName(c.getName());
		e.setType(c.getType());
		return e;
	}

	private CustomerEntity findEntity(Customer c) {
		Long id = c.getId();
		if (id != null)
			return findEntityByPrimaryKey(id);
		return findEntityByName(c.getName());
	}

	private CustomerEntity deactivate(CustomerEntity e, String name) {
		e.setDeactivatedBy(name);
		e.setDeactivatedOn(ZonedDateTime.now());
		return e;
	}
}