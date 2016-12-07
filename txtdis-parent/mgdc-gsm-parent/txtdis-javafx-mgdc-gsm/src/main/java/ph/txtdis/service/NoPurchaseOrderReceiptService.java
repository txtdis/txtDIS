package ph.txtdis.service;

public interface NoPurchaseOrderReceiptService extends BillableService {

	String getReferencePrompt();

	void updateUponReferenceIdValidation(Long id) throws Exception;
}
