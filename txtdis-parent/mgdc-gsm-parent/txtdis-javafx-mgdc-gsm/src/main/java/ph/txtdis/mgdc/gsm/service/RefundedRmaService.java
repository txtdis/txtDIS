package ph.txtdis.mgdc.gsm.service;

import java.time.LocalDate;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.BillingBasedService;
import ph.txtdis.service.VatableService;

public interface RefundedRmaService //
		extends RmaService, BillingBasedService, VatableService {

	void clearInputtedReturnPaymentData();

	String[] getCheckDetails();

	String getInvoiceNo();

	void print() throws Exception;

	void saveReturnPaymentData(LocalDate d) throws Information, Exception;

	void updateUponCheckIdValidation(String bank, Long checkId) throws Exception;

	void updateUponInvoiceNoValidation(String prefix, Long id, String suffix) throws Exception;
}
