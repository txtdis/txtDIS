package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface GoodCreditStandingPerRouteCustomerService {

	List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route r);
}
