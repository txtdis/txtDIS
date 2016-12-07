package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;

public interface GoodCreditStandingPerRouteCustomerService {

	List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route r);
}
