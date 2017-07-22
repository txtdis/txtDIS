package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Booking;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.VerifiedSalesOrderService;
import ph.txtdis.service.CustomerSearchableService;

public interface BookingService //
		extends BillableService, CustomerSearchableService, VerifiedSalesOrderService {

	boolean canInvalidSalesOrderBeOverriden();

	BigDecimal getVat();

	BigDecimal getVatable();

	void invalidateAwaitingApproval(String badCreditMessage);

	List<Booking> listUnpicked(LocalDate d);

	void overrideInvalidation() throws Information, Exception;

	void setCustomerRelatedData();

	void updateUponCustomerIdValidation(Long id) throws Exception;
}
