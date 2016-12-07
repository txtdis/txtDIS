package ph.txtdis.service;

public interface ReceivingReportService extends BillableService, ReceivingService {

	boolean isReceivingReportModifiable();

	boolean isSalesOrderReturnable();

	void updateUponReferenceIdValidation(long id) throws Exception;
}
