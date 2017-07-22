package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;

public interface CustomerValidationService //
		extends CustomerService {

	void cancelAllCustomerDiscountsIfMonthlyAverageIsLessthanRequired(BigDecimal noOfMonths, BigDecimal requiredQty);

	void deactivateAllNonBuyingOutletsAfterThePrescribedPeriod(BigDecimal noOfMonths);
}
