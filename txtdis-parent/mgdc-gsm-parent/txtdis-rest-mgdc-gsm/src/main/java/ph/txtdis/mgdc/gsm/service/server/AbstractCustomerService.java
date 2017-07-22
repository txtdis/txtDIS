package ph.txtdis.mgdc.gsm.service.server;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.FINANCIAL;
import static ph.txtdis.type.PartnerType.OUTLET;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.CreditDetailEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.repository.CustomerRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PartnerType;

public abstract class AbstractCustomerService //
		extends AbstractSpunSavedKeyedService<CustomerRepository, CustomerEntity, Customer, Long> //
		implements CustomerService {

	@Override
	public boolean canPayPostDatedCheck(CustomerEntity e, LocalDate d) {
		CreditDetailEntity credit = latestCreditDetail(e, d);
		return credit == null || credit.getTermInDays() == 0 && credit.getGracePeriodInDays() > 0;
	}

	private CreditDetailEntity latestCreditDetail(CustomerEntity e, LocalDate d) {
		List<CreditDetailEntity> credit = e.getCreditDetails();
		return credit.isEmpty() ? null //
				: credit.stream().collect(maxBy(comparing(CreditDetailEntity::getStartDate))).get();
	}

	@Override
	public Customer findByPrimaryKey(Long id) {
		CustomerEntity e = repository.findOne(id);
		return toModel(e);
	}

	@Override
	public List<Customer> findAll() {
		List<CustomerEntity> l = repository.findByOrderByNameAsc();
		return toBasicInfoOnlyList(l);
	}

	private List<Customer> toBasicInfoOnlyList(List<CustomerEntity> l) {
		return l == null ? null //
				: l.stream().map(c -> basicInfoOnly(c)) //
						.collect(toList());
	}

	protected Customer basicInfoOnly(CustomerEntity e) {
		return idAndNameOnly(e);
	}

	@Override
	public List<Customer> findBanks() {
		return findAllByType(FINANCIAL);
	}

	protected List<Customer> findAllByType(PartnerType type) {
		List<CustomerEntity> l = findEntitiesByType(type);
		return l.stream().map(c -> idAndNameOnly(c)).collect(toList());
	}

	private List<CustomerEntity> findEntitiesByType(PartnerType type) {
		return repository.findByDeactivatedOnNullAndTypeOrderByNameAsc(type);
	}

	protected Customer idAndNameOnly(CustomerEntity e) {
		if (e == null)
			return null;
		Customer c = new Customer();
		c.setId(e.getId());
		c.setName(e.getName());
		return c;
	}

	@Override
	public Customer findByName(String name) {
		return toModel(findEntityByName(name));
	}

	protected Customer findByKey(String key) {
		return toModel(findEntityByKey(key));
	}

	private CustomerEntity findEntityByKey(String key) {
		return repository.findOne(Long.valueOf(key));
	}

	@Override
	public CustomerEntity findEntity(Billable b) {
		Long id = b.getCustomerId();
		return id == null ? findEntityByName(b.getCustomerName()) : findEntityByPrimaryKey(id);
	}

	@Override
	public CustomerEntity findEntityByName(String name) {
		return repository.findByNameIgnoreCase(name);
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
						.collect(toList());
	}

	@Override
	public List<Customer> searchByName(String name) {
		List<CustomerEntity> l = repository.findByNameContainingOrderByNameAsc(name);
		return l == null ? null
				: l.stream().map(c -> basicInfoOnly(c)) //
						.collect(toList());
	}

	@Override
	public Customer toBank(String id) {
		return findByKey(id);
	}

	@Override
	public CustomerEntity toBankEntity(String id) {
		return findEntityByKey(id);
	}

	@Override
	protected Customer toModel(CustomerEntity e) {
		return e == null ? null : newCustomer(e);
	}

	protected Customer newCustomer(CustomerEntity e) {
		Customer c = basicInfoOnly(e);
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		c.setLastModifiedBy(e.getLastModifiedBy());
		c.setLastModifiedOn(e.getLastModifiedOn());
		c.setType(e.getType());
		c.setParent(idAndNameOnly(e.getParent()));
		return c;
	}

	@Override
	public CustomerEntity toEntity(Customer c) {
		return c == null ? null : entity(c);
	}

	protected CustomerEntity entity(Customer c) {
		CustomerEntity e = findEntity(c);
		if (e == null)
			e = new CustomerEntity();
		if (e.getDeactivatedBy() == null && c.getDeactivatedBy() != null)
			return deactivate(e, c.getDeactivatedBy());
		e.setName(c.getName());
		e.setType(c.getType());
		return e;
	}

	protected CustomerEntity findEntity(Customer c) {
		Long id = c.getId();
		if (id != null)
			return findEntityByPrimaryKey(id);
		return findEntityByName(c.getName());
	}

	protected CustomerEntity deactivate(CustomerEntity e, String name) {
		e.setDeactivatedBy(name);
		e.setDeactivatedOn(ZonedDateTime.now());
		return e;
	}
}
