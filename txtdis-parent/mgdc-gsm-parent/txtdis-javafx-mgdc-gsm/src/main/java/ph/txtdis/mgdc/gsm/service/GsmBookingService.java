package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.VerifiedSalesOrderService;
import ph.txtdis.service.LatestApproved;
import ph.txtdis.service.SavableAsExcelService;
import ph.txtdis.type.ModuleType;

import java.util.List;

public interface GsmBookingService //
	extends BookingService,
	ChangeableSalesOrderDetailsService,
	GoodCreditStandingPerRouteCustomerService,
	ItineraryService,
	LatestApproved,
	VerifiedSalesOrderService,
	SavableAsExcelService<Customer>,
	VerifiedLoadOrderService {

	Billable findUncorrectedInvalidBilling();

	ModuleType getType();

	boolean isLoadOrder();

	List<String> listExTrucksWithoutLoadOrders();

	void setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(String truck) throws Exception;

	boolean setType(ModuleType type);
}
