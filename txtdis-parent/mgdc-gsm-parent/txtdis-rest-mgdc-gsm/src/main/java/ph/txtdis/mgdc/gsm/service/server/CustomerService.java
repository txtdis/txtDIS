package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.SpunSavedKeyedService;

import java.time.LocalDate;
import java.util.List;

public interface CustomerService //
	extends SpunSavedKeyedService<CustomerEntity, Customer, Long> {

	boolean canPayPostDatedCheck(CustomerEntity e, LocalDate d);

	List<Customer> findAll();

	List<Customer> findBanks();

	Customer findByName(String name);

	CustomerEntity findEntity(Billable b);

	CustomerEntity findEntityByName(String name);

	List<Customer> listOutletIdsAndNames();

	List<Customer> listOutlets();

	List<Customer> searchByName(String name);

	Customer toBank(String id);

	CustomerEntity toBankEntity(String name);

	CustomerEntity toEntity(Customer c);
}
