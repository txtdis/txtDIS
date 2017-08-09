package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.io.IOException;

public interface ItineraryService //
	extends GoodCreditStandingPerRouteCustomerService {

	@SuppressWarnings("unchecked")
	void saveAsExcel(AppTable<Customer>... tables) throws IOException;
}
