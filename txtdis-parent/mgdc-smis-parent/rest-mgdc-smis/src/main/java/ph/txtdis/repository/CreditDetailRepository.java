package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CreditDetail;

@Repository("creditDetailRepository")
public interface CreditDetailRepository extends CrudRepository<CreditDetail, Long> {
}
