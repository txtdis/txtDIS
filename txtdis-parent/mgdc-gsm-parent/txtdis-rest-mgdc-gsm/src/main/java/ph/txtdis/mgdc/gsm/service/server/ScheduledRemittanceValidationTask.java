package ph.txtdis.mgdc.gsm.service.server;

public interface ScheduledRemittanceValidationTask {

	void voidAllUnvalidatedAfterPrescribedPeriodsSincePaymentAndCreationHaveBothExpired();
}