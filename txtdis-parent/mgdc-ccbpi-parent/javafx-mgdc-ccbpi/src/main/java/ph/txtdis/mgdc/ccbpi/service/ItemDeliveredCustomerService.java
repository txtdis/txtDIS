package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.DateValidated;
import ph.txtdis.service.SavableAsExcelService;

public interface ItemDeliveredCustomerService //
		extends DateValidated, SavableAsExcelService<Customer> {

	boolean areDeliveriesBooked(Customer c, LocalDate orderDate);

	boolean areDeliveriesPickedUp(Customer c, LocalDate orderDate);

	Customer findByVendorId(Long id) throws Exception;

	Route getRoute(Customer c, LocalDate d);

	String getSeller(Customer c, LocalDate d);
}
