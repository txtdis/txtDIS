package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BillableDetailEntity;

@Repository("billingDetailRepository")
public interface BillingDetailRepository extends CrudRepository<BillableDetailEntity, Long> {

	List<BillableDetailEntity> findByBillingOrderDateGreaterThanEqualOrderByItemAsc(@Param("date") LocalDate d);
}
