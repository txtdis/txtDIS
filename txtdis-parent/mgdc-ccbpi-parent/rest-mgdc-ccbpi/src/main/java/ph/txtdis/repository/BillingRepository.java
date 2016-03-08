package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Billing;

@Repository("billingRepository")
public interface BillingRepository extends SpunRepository<Billing, Long> {

	List<Billing> findByOrderDateBetweenOrderByOrderDateAsc(@Param("start") LocalDate s, @Param("end") LocalDate e);

	// next S/O for assignment
	Billing findFirstByOrderDateAndIdGreaterThanOrderByIdAsc(@Param("now") LocalDate d, @Param("id") Long id);

	// previous S/O for assignment
	Billing findFirstByOrderDateAndIdLessThanOrderByIdDesc(@Param("now") LocalDate d, @Param("id") Long id);

	// first S/O for assignment
	Billing findFirstByOrderDateOrderByIdAsc(@Param("now") LocalDate d);

	// last S/O for assignment
	Billing findFirstByOrderDateOrderByIdDesc(@Param("now") LocalDate d);
}
