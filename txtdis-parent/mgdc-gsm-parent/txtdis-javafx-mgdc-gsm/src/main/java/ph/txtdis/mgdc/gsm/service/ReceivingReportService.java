package ph.txtdis.mgdc.gsm.service;

public interface ReceivingReportService extends BillableService, ReceivingService {

	boolean isReceivingReportModifiable();

	boolean isSalesOrderReturnable();

	void updateUponReferenceIdValidation(long id) throws Exception;
}
