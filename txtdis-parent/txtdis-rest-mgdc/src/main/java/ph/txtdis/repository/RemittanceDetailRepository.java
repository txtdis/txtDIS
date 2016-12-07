package ph.txtdis.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.RemittanceDetailEntity;
import ph.txtdis.dto.Remittance;

@Repository("remittanceDetailRepository")
public interface RemittanceDetailRepository extends CrudRepository<RemittanceDetailEntity, Long> {

	List<RemittanceDetailEntity> findByBilling(@Param("billing") BillableEntity b);

	// Payments made with post-dated checks, which matured today
	List<RemittanceDetailEntity> findByRemittancePaymentDateLessThanEqualAndRemittanceCheckIdNotNullAndBillingFullyPaidFalseAndBillingUnpaidValue(
			@Param("now") LocalDate d, @Param("zero") BigDecimal u);

	Remittance findFirstByBillingIdOrderByIdDesc(@Param("id") Long id);
}
