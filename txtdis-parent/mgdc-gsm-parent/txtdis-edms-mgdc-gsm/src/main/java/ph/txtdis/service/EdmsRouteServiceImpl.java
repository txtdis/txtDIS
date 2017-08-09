package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service("routeService")
public class EdmsRouteServiceImpl //
	implements EdmsRouteService {

	@Autowired
	private EdmsService edmsService;

	@Autowired
	private EdmsSellerService sellerService;

	@Override
	public List<Route> findByOrderByNameAsc() {
		return sellerService.getAll().map(s -> toRoute(s)).collect(Collectors.toList());
	}

	private Route toRoute(EdmsSeller s) {
		Route r = toNameOnlyRouteFromSeller(s.getCode());
		r.setSellerHistory(getSellerHistory(s));
		return r;
	}

	@Override
	public Route toNameOnlyRouteFromSeller(String sellerCode) {
		EdmsSeller s = sellerService.findByCode(sellerCode);
		return toNameOnlyRouteFromTruck(s.getTruckCode());
	}

	private List<Account> getSellerHistory(EdmsSeller s) {
		Account a = new Account();
		a.setSeller(sellerService.getUsername(s));
		a.setStartDate(edmsService.goLiveDate());
		return asList(a);
	}

	@Override
	public Route toNameOnlyRouteFromTruck(String truckCode) {
		Route r = new Route();
		r.setName(truckCode);
		return r;
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