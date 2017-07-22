package ph.txtdis.mgdc.gsm.service;

import java.io.IOException;

import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface ItineraryService //
		extends GoodCreditStandingPerRouteCustomerService {

	@SuppressWarnings("unchecked")
	void saveAsExcel(AppTable<Customer>... tables) throws IOException;
}
