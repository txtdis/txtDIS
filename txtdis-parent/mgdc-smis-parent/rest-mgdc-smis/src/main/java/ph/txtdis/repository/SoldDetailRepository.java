package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BillingDetail;

@Repository("soldDetailRepository")
public interface SoldDetailRepository extends CrudRepository<BillingDetail, Long> {
}
