package ph.txtdis.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.DateTimeUtils;

@Service("itineraryService")
public class ItineraryServiceImpl implements ItineraryService {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ExcelWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	private Route selectedRoute;

	@Override
	public List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route selectedRoute) {
		this.selectedRoute = selectedRoute;
		return customerService.listByScheduledRouteAndGoodCreditStanding(selectedRoute);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Customer>... tables) throws IOException {
		excel.filename(excelName()).sheetname(sheetName()).table(tables).write();
	}

	private String excelName() {
		return "Route Itinerary for " + modulePrefix + " " + selectedRoute + " on "
				+ DateTimeUtils.toDateDisplay(syncService.getServerDate());
	}

	private String sheetName() {
		return selectedRoute + "." + syncService.getServerDate();
	}
}
