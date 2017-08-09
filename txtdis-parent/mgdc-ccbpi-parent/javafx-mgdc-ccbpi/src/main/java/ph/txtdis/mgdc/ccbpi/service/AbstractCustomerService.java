package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.util.ClientTypeMap;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractCustomerService //
	implements CustomerService {

	@Autowired
	private RestClientService<Customer> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	private Customer customer;

	private List<Customer> customers;

	public AbstractCustomerService() {
		reset();
	}

	@Override
	public void reset() {
		customer = null;
		customers = null;
	}

	@Override
	public boolean canApprove() {
		return isUser(MANAGER);
	}

	@Override
	public Customer findActive(Long id) throws Exception {
		Customer c = findById(id);
		if (c.getDeactivatedOn() != null)
			throw new DeactivatedException(c.getName());
		return c;
	}

	@Override
	public String getAlternateName() {
		return capitalize(getModuleName());
	}

	@Override
	public String getModuleName() {
		return "customer";
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Customer get() {
		if (customer == null)
			customer = new Customer();
		return customer;
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getDecidedBy() {
		return username();
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
	public void setId(Long id) {
		get().setId(id);
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

	public RestClientService<Customer> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getRemarks() {
		return "";
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getUsername() {
		return username();
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

	protected List<Customer> getList(String endpoint) throws Exception {
		return getRestClientService().module(getModuleName()).getList(endpoint);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Customer> getRestClientService() {
		return restClientService;
	}

	@Override
	public List<Customer> search(String text) throws Exception {
		return customers = getList(text.isEmpty() ? "" : "/search?name=" + text);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		customer = (Customer) t;
	}

	@Override
	public void setType(PartnerType type) {
		get().setType(type);
	}
}
