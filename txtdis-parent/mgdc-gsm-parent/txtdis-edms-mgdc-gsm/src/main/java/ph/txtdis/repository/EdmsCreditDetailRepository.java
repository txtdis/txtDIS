package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsCreditDetail;

@Repository("edmsCreditDetailRepository")
public interface EdmsCreditDetailRepository extends CrudRepository<EdmsCreditDetail, Long> {

	EdmsCreditDetail findByCustomerCode(@Param("code") String c);
}
