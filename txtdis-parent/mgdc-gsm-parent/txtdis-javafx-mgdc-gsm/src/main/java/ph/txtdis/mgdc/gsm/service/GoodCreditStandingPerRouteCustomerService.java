package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.util.List;

public interface GoodCreditStandingPerRouteCustomerService {

	List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route r);
}
