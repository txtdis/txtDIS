package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface EdmsCustomerService //
		extends SavedService<Customer> {

	List<Customer> list();

	List<Customer> listonAction();

	String getCode(Billable b);

	String getName(EdmsInvoice i);

	List<String> getProvinces();

	List<String> getCities(String province);
}
