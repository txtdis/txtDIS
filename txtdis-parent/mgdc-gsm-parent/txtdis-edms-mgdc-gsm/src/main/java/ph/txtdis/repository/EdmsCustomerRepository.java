package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsCustomer;

@Repository("edmsCustomerRepository")
public interface EdmsCustomerRepository extends CodeNameRepository<EdmsCustomer> {

	List<EdmsCustomer> findByProvinceContainingIgnoreCase(@Param("province") String p);

	List<EdmsCustomer> findByCityContainingIgnoreCase(@Param("city") String c);
}
