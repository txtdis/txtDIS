package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.info.Information;

public interface BookingService
		extends BillableService, BilledAllPickedSalesOrder, CustomerSearchableService, DecisionNeeded {

	boolean canInvalidSalesOrderBeOverriden();

	BigDecimal getVat();

	BigDecimal getVatable();

	void invalidateAwaitingApproval(String badCreditMessage);

	void overrideInvalidation() throws Information, Exception;

	void setCustomerRelatedData();

	void updateSummaries(List<BillableDetail> items);

	void updateUponCustomerIdValidation(Long id) throws Exception;
}
