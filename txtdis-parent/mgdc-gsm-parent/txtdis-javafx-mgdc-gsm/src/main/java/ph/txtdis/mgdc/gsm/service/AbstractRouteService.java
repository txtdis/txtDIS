package ph.txtdis.mgdc.gsm.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Truck;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.RouteService;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SyncService;
import ph.txtdis.service.TruckService;
import ph.txtdis.service.UserService;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractRouteService //
		implements RouteService {

	private static final String AGING_RECEIVABLE = "agingReceivable";

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SavingService<Route> savingService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private TruckService truckService;

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
	public Route findById(String id) throws Exception {
		return set(readOnlyService.module(getModuleName()).getOne("/find?id=" + id));
	}

	@Override
	public Route findByIds(String[] ids) throws Exception {
		return launcedFromAgingReceivable(ids) ? viaCustomer(ids) : findById(routeId(ids));
	}

	private boolean launcedFromAgingReceivable(String[] ids) {
		return ids[2] == null ? false : ids[2].equals(AGING_RECEIVABLE);
	}

	private Route viaCustomer(String[] ids) throws Exception {
		Customer customer = customerService.findByOrderNo(ids[0]);
		return route = customerService.getRoute(customer, syncService.getServerDate());
	}

	private String routeId(String[] ids) {
		return ids[0];
	}

	@Override
	public String getModuleName() {
		return "route";
	}

	@Override
	public ReadOnlyService<Route> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public List<Account> getSellerHistory() {
		return get().getSellerHistory() == null ? Collections.emptyList() : get().getSellerHistory();
	}

	private Route get() {
		if (route == null)
			route = new Route();
		return route;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + RouteService.super.getTitleName();
	}

	@Override
	public List<Truck> getTrucks() {
		return truckService.list();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listUsers() throws Exception {
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
	public void reset() {
		route = null;
	}

	@Override
	public Route save(String name) throws Information, Exception {
		Route r = get();
		r.setName(name);
		return save(r);
	}

	private Route save(Route r) throws Exception {
		return set(savingService.module(getModuleName()).save(r));
	}

	private Route set(Route r) {
		return route = r;
	}

	@Override
	public Account save(String seller, LocalDate date) throws Information, Exception {
		updateSellerHistory(seller, date);
		save(get());
		return getSellerHistory().stream().filter(s -> s.getSeller().equals(seller) && s.getStartDate().equals(date)).findAny().get();
	}

	private List<Account> updateSellerHistory(String seller, LocalDate startDate) {
		List<Account> list = getSellerHistory();
		list.add(createAccount(seller, startDate));
		get().setSellerHistory(list);
		return list;
	}

	private Account createAccount(String seller, LocalDate startDate) {
		Account a = new Account();
		a.setSeller(seller);
		a.setStartDate(startDate);
		return a;
	}
}
