package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Customer;

@Repository("customerRepository")
public interface CustomerRepository extends SpunRepository<Customer, Long> {

	Customer findByMobile(@Param("mobile") String no);
}
