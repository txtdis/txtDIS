package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Booking;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.type.OrderConfirmationType;

public interface OrderConfirmationService //
	extends ShippedBillableService {

	BigDecimal getDeliveredValue(String collector, LocalDate start, LocalDate end);

	String getDelivery();

	LocalDate getDeliveryDate();

	Long getSequenceId();

	List<String> listRoutes();

	List<OrderConfirmationType> listTypes();

	List<Booking> listUnpicked(LocalDate pickDate);

	void resetAndSetOrderDate(LocalDate value);

	void setCustomer(Customer c) throws Exception;

	void setCustomerId(Long id);

	void setDeliveryDateUponValidation(LocalDate d) throws Exception;

	void setRoute(String route);

	void updateUponCustomerVendorIdValidation(Long id) throws Exception;

	void updateUponTypeValidation(OrderConfirmationType t) throws Exception;
}
