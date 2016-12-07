package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.dto.Customer;

public interface CustomerService extends SpunService<Customer, Long> {

	Customer find(Long id);

	List<Customer> findBanks();

	Customer findByName(String name);

	Customer findByVendorId(Long id);

	List<Customer> findExTrucks();

	Customer findNoContactDetails();

	Customer findNoDesignation();

	Customer findNoMobileNo();

	Customer findNoStreetAddress();

	Customer findNoSurname();

	Customer findNotCorrectBarangayAddress();

	Customer findNotCorrectCityAddress();

	Customer findNotCorrectProvincialAddress();

	Customer findNotTheSameVisitFrequencyAndSchedule();

	Customer findNotTheSameWeeksOneAndFiveVisitSchedule();

	Customer findNoVisitSchedule();

	Customer getVendor();

	List<Customer> list();

	List<Customer> listByScheduledRouteAndGoodCreditStanding(Long id);

	List<Customer> searchByName(String name);

	Customer toBank(String id);

	CustomerEntity toBankEntity(String string);

	CustomerEntity toEntity(Customer customer);
}
