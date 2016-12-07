package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;
import ph.txtdis.type.DeliveryType;

@Service("routeService")
public class RouteServiceImpl implements RouteService {

	private static Logger logger = getLogger(RouteServiceImpl.class);

	@Autowired
	private EdmsService edmsService;

	@Autowired
	private SellerService sellerService;

	@Override
	public Route toNameOnlyDTOFromSeller(String sellerCode) {
		EdmsSeller s = sellerService.findByCode(sellerCode);
		return toNameOnlyDTOFromTruck(s.getTruckCode());
	}

	@Override
	public Route toNameOnlyDTOFromTruck(String truckCode) {
		Route r = new Route();
		r.setName(truckCode);
		return r;
	}

	private Route toRoute(EdmsSeller s) {
		Route r = toNameOnlyDTOFromSeller(s.getCode());
		r.setSellerHistory(getSellerHistory(s));
		r.setType(getDeliveryType(s));
		logger.info("\n\t\t\t\tRoute: " + r.getName() + " - " + r.getType());
		return r;
	}

	private List<Account> getSellerHistory(EdmsSeller s) {
		Account a = new Account();
		a.setSeller(sellerService.getUsername(s));
		a.setStartDate(edmsService.goLiveDate());
		logger.info("\n\t\t\t\tSellerHistory: " + a.getSeller() + " - " + a.getStartDate());
		return Arrays.asList(a);
	}

	private DeliveryType getDeliveryType(EdmsSeller s) {
		return s.getTruckCode().isEmpty() ? DeliveryType.PICK_UP : DeliveryType.DELIVERED;
	}

	@Override
	public List<Route> findByOrderByNameAsc() {
		return sellerService.getAll().map(s -> toRoute(s)).collect(Collectors.toList());
	}

	@Override
	public Route findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Route save(Route t) {
		// TODO Auto-generated method stub
		return null;
	}
}