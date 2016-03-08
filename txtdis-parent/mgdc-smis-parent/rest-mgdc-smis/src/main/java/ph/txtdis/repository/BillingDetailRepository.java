package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BillingDetail;

@Repository("billingDetailRepository")
public interface BillingDetailRepository extends CrudRepository<BillingDetail, Long> {

	List<BillingDetail> findByBillingOrderDateGreaterThanEqualOrderByItemAsc(@Param("date") LocalDate d);
}
