package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.DateValidated;
import ph.txtdis.service.SavableAsExcelService;
import ph.txtdis.type.BillingType;

import java.time.LocalDate;

public interface ItemDeliveredCustomerService //
	extends DateValidated,
	SavableAsExcelService<Customer>,
	ItemFamilyLimited {

	boolean areDeliveriesBooked(Customer c, LocalDate orderDate);

	boolean areDeliveriesPickedUp(Customer c, LocalDate orderDate);

	Customer findByVendorId(Long id) throws Exception;

	BillingType getBillingType(Billable b) throws Exception;

	Route getRoute(Customer c, LocalDate d);

	String getSeller(Customer c, LocalDate d);

	boolean isDeliveryScheduledOnThisDate(Customer c, LocalDate d);
}
