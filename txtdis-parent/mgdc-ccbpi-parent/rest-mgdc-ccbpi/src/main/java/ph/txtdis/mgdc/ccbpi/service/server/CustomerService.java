package ph.txtdis.mgdc.ccbpi.service.server;

import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.SpunSavedKeyedService;

public interface CustomerService //
	extends SpunSavedKeyedService<CustomerEntity, Customer, Long> {

	List<Customer> findAll();

	List<Customer> findBanks();

	Customer findByName(String name);

	Customer findByVendorId(Long id);

	CustomerEntity findEntity(Billable b);

	CustomerEntity findEntityByName(String name);

	List<Customer> listOutletIdsAndNames();

	List<Customer> listOutlets();

	List<Customer> searchByName(String name);

	Customer toBank(String id);

	CustomerEntity toBankEntity(String name);

	CustomerEntity toEntity(Customer c);
}
