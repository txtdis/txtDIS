package ph.txtdis.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.DeliveryType;
import ph.txtdis.util.TypeMap;

@Service
public class RouteService implements Iconed, Listed<Route>, UniquelyNamed<Route> {

	private static final String AGING_RECEIVABLE = "agingReceivable";

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SavingService<Route> savingService;

	@Autowired
	private ReadOnlyService<Route> readOnlyService;

	@Autowired
	private UserService userService;

	private Route route;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public boolean isOffSite() {
		return server.isOffSite();
	}

	public Route find(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return route = readOnlyService.module(getModule()).getOne("/find?id=" + id);
	}

	public Route find(String[] ids) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		return launcedFromAgingReceivable(ids) ? viaCustomer(ids) : find(routeId(ids));
	}

	@Override
	public String getModule() {
		return "route";
	}

	@Override
	public ReadOnlyService<Route> getReadOnlyService() {
		return readOnlyService;
	}

	public List<Account> getSellerHistory() {
		return route == null || route.getSellerHistory() == null ? new ArrayList<>() : route.getSellerHistory();
	}

	public List<String> listUsers() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return userService.list().stream().map(u -> u.getUsername()).collect(Collectors.toList());
	}

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

	private Account createAccount(String seller, LocalDate startDate) {
		Account account = new Account();
		account.setSeller(seller);
		account.setStartDate(startDate);
		return account;
	}

	private boolean launcedFromAgingReceivable(String[] ids) {
		return ids[2] == null ? false : ids[2].equals(AGING_RECEIVABLE);
	}

	private String routeId(String[] ids) {
		return ids[0];
	}

	private List<Account> updatedSellerHistory(String seller, LocalDate startDate) {
		List<Account> list = getSellerHistory();
		list.add(createAccount(seller, startDate));
		return list;
	}

	private Route viaCustomer(String[] ids) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Customer c = customerService.find(ids[0]);
		return route = c.getRoute(LocalDate.now());
	}
}
