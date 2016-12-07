package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CreditDetailEntity;

@Repository("creditDetailRepository")
public interface CreditDetailRepository extends CrudRepository<CreditDetailEntity, Long> {
}
