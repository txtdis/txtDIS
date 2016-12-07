package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.type.BillableType;

public interface BillingService extends BillableService {

	BigDecimal getBalance();

	Billable getBilling(String prefix, Long id, String suffix) throws Exception;

	String getBillingPrompt();

	BigDecimal getVat();

	BigDecimal getVatable();

	List<Billable> listAged(Customer customer);

	List<Billable> listAging(Customer customer);

	void setType(BillableType type);

	void updateSummaries(List<BillableDetail> items);

	void updateUponReferenceIdValidation(Long id) throws Exception;

	void updateUponCustomerIdValidation(Long id) throws Exception;

	void updateUponOrderNoValidation(String prefix, Long numId, String suffix) throws Exception;
}
