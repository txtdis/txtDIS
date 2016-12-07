package ph.txtdis.service;

import java.io.IOException;

import ph.txtdis.dto.Customer;
import ph.txtdis.fx.table.AppTable;

public interface ItineraryService extends GoodCreditStandingPerRouteCustomerService {

	@SuppressWarnings("unchecked")
	void saveAsExcel(AppTable<Customer>... tables) throws IOException;
}
