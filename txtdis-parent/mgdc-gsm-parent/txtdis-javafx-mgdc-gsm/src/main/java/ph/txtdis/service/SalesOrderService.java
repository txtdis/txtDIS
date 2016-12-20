package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Customer;
import ph.txtdis.type.BillableType;

public interface SalesOrderService extends BookingService, PickedLoadOrderVerified, Excel<Customer>,
		GoodCreditStandingPerRouteCustomerService, ItineraryService, LatestApproved {

	boolean isExTruck();

	List<String> listUnbookedExTrucks();

	void setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(String truck) throws Exception;

	void setType(BillableType type);
}
