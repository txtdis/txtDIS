package ph.txtdis.mgdc.gsm.service.server;

public interface ScheduledCustomerValidationTask {

	void cancelAllCustomerDiscountsIfMonthlyAverageIsLessThanRequired();

	void deactiveAllNonBuyingOutletsAfterThePrescribedPeriod();
}