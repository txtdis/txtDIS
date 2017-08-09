package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.BillingBasedService;
import ph.txtdis.service.VatableService;
import ph.txtdis.type.ModuleType;

import java.math.BigDecimal;
import java.util.List;

public interface BillingService //
	extends BillableService,
	BillingBasedService,
	VatableService {

	Billable findBilling(String prefix, Long id, String suffix) throws Exception;

	BigDecimal getBalance();

	String getBillingPrompt();

	boolean isAnInvoice();

	List<Billable> listAged(Customer customer);

	List<Billable> listAging(Customer customer);

	void setType(ModuleType type);

	void updateUponCustomerIdValidation(Long id) throws Exception;

	void updateUponOrderNoValidation(String prefix, Long numId, String suffix) throws Exception;

	void updateUponReferenceIdValidation(Long id) throws Exception;
}
