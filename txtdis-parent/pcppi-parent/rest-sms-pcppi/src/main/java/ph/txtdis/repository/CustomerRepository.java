package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerImpl;

@Repository("customerRepository")
public interface CustomerRepository extends SpunRepository<CustomerImpl, Long> {

	CustomerImpl findByMobile(@Param("mobile") String no);
}
