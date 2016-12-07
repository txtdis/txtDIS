package ph.txtdis.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.DeliveryType;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractRouteService implements RouteService {

	private static final String AGING_RECEIVABLE = "agingReceivable";

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<Route> savingService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private UserService userService;

	@Autowired
	protected ReadOnlyService<Route> readOnlyService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private Route route;

	@Override
	public Route find(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return route = readOnlyService.module(getModule()).getOne("/find?id=" + id);
	}

	@Override
	public Route find(String[] ids) throws Exception {
		return launcedFromAgingReceivable(ids) ? viaCustomer(ids) : find(routeId(ids));
	}

	private boolean launcedFromAgingReceivable(String[] ids) {
		return ids[2] == null ? false : ids[2].equals(AGING_RECEIVABLE);
	}

	private Route viaCustomer(String[] ids) throws Exception {
		Customer customer = customerService.find(ids[0]);
		return route = customer.getRoute(syncService.getServerDate());
	}

	private String routeId(String[] ids) {
		return ids[0];
	}

	@Override
	public String getModule() {
		return "route";
	}

	@Override
	public ReadOnlyService<Route> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public List<Account> getSellerHistory() {
		return route == null || route.getSellerHistory() == null ? new ArrayList<>() : route.getSellerHistory();
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + RouteService.super.getTitleText();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public List<String> listUsers() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return userService.list().stream().map(u -> u.getUsername()).collect(Collectors.toList());
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(u -> u.getName()).sorted().collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public Route save(String name, DeliveryType type)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Route r = new Route();
		r.setName(name);
		r.setType(type);
		return savingService.module(getModule()).save(r);
	}

	@Override
	public Account save(String seller, LocalDate date)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		List<Account> list = updatedSellerHistory(seller, date);
		route.setSellerHistory(list);
		route = savingService.module(getModule()).save(route);
		return getSellerHistory().stream().filter(s -> s.getSeller().equals(seller) && s.getStartDate().equals(date))
				.findAny().get();
	}

	private List<Account> updatedSellerHistory(String seller, LocalDate startDate) {
		List<Account> list = getSellerHistory();
		list.add(createAccount(seller, startDate));
		return list;
	}

	private Account createAccount(String seller, LocalDate startDate) {
		Account account = new Account();
		account.setSeller(seller);
		account.setStartDate(startDate);
		return account;
	}
}
