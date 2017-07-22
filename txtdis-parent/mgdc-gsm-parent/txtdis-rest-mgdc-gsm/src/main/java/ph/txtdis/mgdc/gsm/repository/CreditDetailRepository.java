package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.CreditDetailEntity;

@Repository("creditDetailRepository")
public interface CreditDetailRepository extends CrudRepository<CreditDetailEntity, Long> {
}
