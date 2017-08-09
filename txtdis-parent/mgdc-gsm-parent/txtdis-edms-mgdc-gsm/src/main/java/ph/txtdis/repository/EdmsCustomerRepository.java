package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsCustomer;

import java.util.List;

@Repository("edmsCustomerRepository")
public interface EdmsCustomerRepository //
	extends CodeNameRepository<EdmsCustomer> {

	List<EdmsCustomer> findByProvinceContainingIgnoreCase( //
	                                                       @Param("province") String p);

	List<EdmsCustomer> findByCityContainingIgnoreCase( //
	                                                   @Param("city") String c);
}
