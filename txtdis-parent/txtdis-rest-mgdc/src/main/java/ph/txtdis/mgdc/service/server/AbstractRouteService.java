package ph.txtdis.mgdc.service.server;

import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.RouteType.PRE_SELL;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.User;
import ph.txtdis.mgdc.domain.AccountEntity;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.mgdc.repository.AccountRepository;
import ph.txtdis.mgdc.repository.RouteRepository;
import ph.txtdis.service.AbstractCreateNameListService;
import ph.txtdis.service.ServerUserService;

public abstract class AbstractRouteService //
		extends AbstractCreateNameListService<RouteRepository, RouteEntity, Route> //
		implements RouteService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ServerUserService userService;

	@Override
	public Route findByPrimaryKey(Long id) {
		RouteEntity e = repository.findOne(id);
		return toModel(e);
	}

	@Override
	public List<Route> listExTruckRoutes() {
		return list(EX_TRUCK.toString());
	}

	private List<Route> list(String name) {
		List<RouteEntity> l = repository.findByNameStartingWith(name);
		return toModels(l);
	}

	@Override
	public List<Route> listPreSellRoutes() {
		return list(PRE_SELL.toString());
	}

	@Override
	public Route toModel(RouteEntity e) {
		if (e == null)
			return null;
		Route r = new Route();
		r.setId(e.getId());
		r.setName(e.getName());
		r.setSellerHistory(toAccounts(e.getSellerHistory()));
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		return r;
	}

	private List<Account> toAccounts(List<AccountEntity> l) {
		return l == null ? null : l.stream().map(e -> toAccount(e)).collect(Collectors.toList());
	}

	private Account toAccount(AccountEntity e) {
		Account a = new Account();
		a.setId(e.getId());
		a.setSeller(e.getSeller());
		a.setSellerFullName(sellerFullName(e));
		a.setStartDate(e.getStartDate());
		a.setCreatedBy(e.getCreatedBy());
		a.setCreatedOn(e.getCreatedOn());
		return a;
	}

	private String sellerFullName(AccountEntity a) {
		try {
			User u = userService.findByPrimaryKey(a.getSeller());
			return u.getSurname() + ", " + u.getUsername();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public RouteEntity toEntity(Route r) {
		RouteEntity e = findSavedEntity(r);
		if (e == null)
			e = newEntity(r);
		if (isSellerHistoryAppended(e, r))
			e.setSellerHistory(toEntities(r));
		return e;
	}

	private RouteEntity findSavedEntity(Route t) {
		if (t.getId() != null)
			return repository.findOne(t.getId());
		return repository.findByNameIgnoreCase(t.getName());
	}

	private RouteEntity newEntity(Route r) {
		RouteEntity e = new RouteEntity();
		e.setName(r.getName());
		return e;
	}

	private boolean isSellerHistoryAppended(RouteEntity e, Route r) {
		return r.getSellerHistory().size() > e.getSellerHistory().size();
	}

	private List<AccountEntity> toEntities(Route r) {
		return r.getSellerHistory().stream().map(a -> toEntity(a)).collect(Collectors.toList());
	}

	private AccountEntity toEntity(Account a) {
		AccountEntity e = findSavedEntity(a);
		return e != null ? e : newEntity(a);
	}

	private AccountEntity findSavedEntity(Account a) {
		Long id = a.getId();
		return id == null ? null : accountRepository.findOne(id);
	}

	private AccountEntity newEntity(Account t) {
		AccountEntity e = new AccountEntity();
		e.setSeller(t.getSeller());
		e.setStartDate(t.getStartDate());
		return e;
	}
}