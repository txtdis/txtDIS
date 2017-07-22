package ph.txtdis.mgdc.gsm.service;

public interface EditableBillingNoService {

	boolean canEditInvoiceNo();

	void setOrderNoAndRemarksBeforeInvoiceNoEdit();

	void updateRemarksAfterInvoiceNoEdit();
}
