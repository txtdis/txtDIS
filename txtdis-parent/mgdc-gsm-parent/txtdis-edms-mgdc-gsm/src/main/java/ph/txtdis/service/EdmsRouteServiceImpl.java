package ph.txtdis.service;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;

@Service("routeService")
public class EdmsRouteServiceImpl //
		implements EdmsRouteService {

	@Autowired
	private EdmsService edmsService;

	@Autowired
	private EdmsSellerService sellerService;

	@Override
	public Route toNameOnlyRouteFromSeller(String sellerCode) {
		EdmsSeller s = sellerService.findByCode(sellerCode);
		return toNameOnlyRouteFromTruck(s.getTruckCode());
	}

	@Override
	public Route toNameOnlyRouteFromTruck(String truckCode) {
		Route r = new Route();
		r.setName(truckCode);
		return r;
	}

	private Route toRoute(EdmsSeller s) {
		Route r = toNameOnlyRouteFromSeller(s.getCode());
		r.setSellerHistory(getSellerHistory(s));
		return r;
	}

	private List<Account> getSellerHistory(EdmsSeller s) {
		Account a = new Account();
		a.setSeller(sellerService.getUsername(s));
		a.setStartDate(edmsService.goLiveDate());
		return asList(a);
	}

	@Override
	public List<Route> findByOrderByNameAsc() {
		return sellerService.getAll().map(s -> toRoute(s)).collect(Collectors.toList());
	}

	@Override
	public Route findByName(String name) {
		return null;
	}

	@Override
	public Route save(Route r) {
		return sellerService.save(r);
	}
}