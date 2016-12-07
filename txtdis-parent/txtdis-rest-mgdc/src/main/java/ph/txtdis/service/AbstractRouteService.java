package ph.txtdis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.AccountEntity;
import ph.txtdis.domain.RouteEntity;
import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;
import ph.txtdis.repository.AccountRepository;
import ph.txtdis.repository.RouteRepository;
import ph.txtdis.type.DeliveryType;

public abstract class AbstractRouteService extends AbstractCreateNameListService<RouteRepository, RouteEntity, Route>
		implements PrimaryRouteService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Route findById(Long id) {
		RouteEntity e = repository.findOne(id);
		return toDTO(e);
	}

	@Override
	public List<RouteEntity> listDelivered() {
		return repository.findByType(DeliveryType.DELIVERED);
	}

	@Override
	public List<Route> listExTruckRoutes() {
		return list("EX-TRUCK");
	}

	private List<Route> list(String name) {
		List<RouteEntity> l = repository.findByNameStartingWith(name);
		return toList(l);
	}

	@Override
	public List<Route> listPreSellRoutes() {
		return list("PRE-SELL");
	}

	@Override
	public Route toDTO(RouteEntity e) {
		if (e == null)
			return null;
		Route r = new Route();
		r.setId(e.getId());
		r.setName(e.getName());
		r.setType(e.getType());
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
		a.setStartDate(e.getStartDate());
		a.setCreatedBy(e.getCreatedBy());
		a.setCreatedOn(e.getCreatedOn());
		return a;
	}

	@Override
	public RouteEntity toEntity(Route t) {
		RouteEntity e = findSavedEntity(t);
		return e != null ? e : newEntity(t);
	}

	private RouteEntity findSavedEntity(Route t) {
		if (t.getId() != null)
			return repository.findOne(t.getId());
		return repository.findByNameIgnoreCase(t.getName());
	}

	private RouteEntity newEntity(Route t) {
		RouteEntity e = new RouteEntity();
		e.setName(t.getName());
		e.setType(t.getType());
		e.setSellerHistory(toAccountEntities(t.getSellerHistory()));
		return e;
	}

	private List<AccountEntity> toAccountEntities(List<Account> l) {
		return l == null ? null : l.stream().map(e -> toEntity(e)).collect(Collectors.toList());
	}

	private AccountEntity toEntity(Account t) {
		AccountEntity e = findSavedEntity(t);
		return e != null ? e : newEntity(t);
	}

	private AccountEntity findSavedEntity(Account t) {
		Long id = t.getId();
		return id == null ? null : accountRepository.findOne(id);
	}

	private AccountEntity newEntity(Account t) {
		AccountEntity e = new AccountEntity();
		e.setSeller(t.getSeller());
		e.setStartDate(t.getStartDate());
		return e;
	}
}