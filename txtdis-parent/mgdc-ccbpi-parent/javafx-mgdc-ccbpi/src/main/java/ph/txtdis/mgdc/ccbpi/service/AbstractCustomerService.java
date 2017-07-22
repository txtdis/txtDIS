package ph.txtdis.mgdc.ccbpi.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.UserType.MANAGER;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.UserType;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractCustomerService //
		implements CustomerService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Customer> readOnlyCustomerService;

	@Autowired
	private SavingService<Customer> savingService;

	@Autowired
	private SpunKeyedService<Customer, Long> spunService;

	@Autowired
	private ClientTypeMap typeMap;

	private Customer customer;

	private List<Customer> customers;

	public AbstractCustomerService() {
		reset();
	}

	@Override
	public boolean canApprove() {
		return isUser(MANAGER);
	}

	protected boolean isUser(UserType t) {
		return credentialService.isUser(t);
	}

	@Override
	public Customer findActive(Long id) throws Exception {
		Customer c = findById(id);
		if (c.getDeactivatedOn() != null)
			throw new DeactivatedException(c.getName());
		return c;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Customer get() {
		if (customer == null)
			customer = new Customer();
		return customer;
	}

	@Override
	public String getAlternateName() {
		return capitalize(getModuleName());
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getDecidedBy() {
		return credentialService.username();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return ZonedDateTime.now();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return get().getDeactivatedOn() != null;
	}

	@Override
	public String getLastModifiedBy() {
		return get().getLastModifiedBy();
	}

	@Override
	public ZonedDateTime getLastModifiedOn() {
		return get().getLastModifiedOn();
	}

	@Override
	public ReadOnlyService<Customer> getListedReadOnlyService() {
		return getReadOnlyService();
	}

	@Override
	public String getModuleName() {
		return "customer";
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Customer> getReadOnlyService() {
		return readOnlyCustomerService;
	}

	@Override
	public String getRemarks() {
		return "";
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Customer> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<Customer, Long> getSpunService() {
		return spunService;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getUsername() {
		return credentialService.username();
	}

	@Override
	public List<Customer> list() {
		return customers;
	}

	@Override
	public List<String> listBanks() {
		try {
			return getList("/banks").stream().map(c -> c.getName()).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public void reset() {
		customer = null;
		customers = null;
	}

	@Override
	public List<Customer> search(String text) throws Exception {
		return customers = getList(text.isEmpty() ? "" : "/search?name=" + text);
	}

	protected List<Customer> getList(String endpoint) throws Exception {
		return getListedReadOnlyService().module(getModuleName()).getList(endpoint);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		customer = (Customer) t;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setType(PartnerType type) {
		get().setType(type);
	}
}
