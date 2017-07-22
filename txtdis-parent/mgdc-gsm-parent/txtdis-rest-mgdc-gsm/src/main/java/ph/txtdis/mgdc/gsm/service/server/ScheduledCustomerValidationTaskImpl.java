package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("scheduledCustomerValidationTask")
public class ScheduledCustomerValidationTaskImpl //
		implements ScheduledCustomerValidationTask {

	@Autowired
	private CustomerValidationService service;

	@Value("${customer.discount.computation.no.of.months}")
	private BigDecimal customerDiscountComputationNoOfMonths;

	@Value("${customer.discount.required.avg.monthly.qty}")
	private BigDecimal customerDiscountRequiredAvgMonthlyQty;

	@Value("${months.without.sales.to.trigger.inactivity}")
	private BigDecimal monthsWithoutSalesToTriggerInactivity;

	private boolean updatedCustomerDiscounts, updatedOutlets;

	@Override
	@Scheduled(cron = "0 15 8/1 * * *")
	public void cancelAllCustomerDiscountsIfMonthlyAverageIsLessThanRequired() {
		if (!updatedCustomerDiscounts) {
			service.cancelAllCustomerDiscountsIfMonthlyAverageIsLessthanRequired( //
					customerDiscountComputationNoOfMonths, customerDiscountRequiredAvgMonthlyQty);
			updatedCustomerDiscounts = true;
		}
	}

	@Override
	@Scheduled(cron = "0 20 8/1 * * *")
	public void deactiveAllNonBuyingOutletsAfterThePrescribedPeriod() {
		if (!updatedOutlets) {
			service.deactivateAllNonBuyingOutletsAfterThePrescribedPeriod(monthsWithoutSalesToTriggerInactivity);
			updatedOutlets = true;
		}
	}
}
