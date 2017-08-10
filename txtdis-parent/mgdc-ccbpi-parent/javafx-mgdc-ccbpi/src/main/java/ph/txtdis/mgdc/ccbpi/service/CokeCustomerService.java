package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.mgdc.ccbpi.dto.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CokeCustomerService //
	extends ItemDeliveredCustomerService,
	CustomerService {

	BigDecimal getCustomerDiscountValue(Long customerId, Long itemId, LocalDate dueDate);

	List<String> listTruckRoutes();

	List<String> listRoutes();

	Customer save(Customer c, Long vendorId, String name, String route, String truckRoute) throws Exception;
}
