package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Customer;

public interface CustomerService extends IdService<Customer, Long> {

	List<Customer> list();

	List<Customer> listExTrucks();

	String getCode(Billable i);

	String getName(EdmsInvoice i);

	List<String> getProvinces();

	List<String> getCities(String province);
}
