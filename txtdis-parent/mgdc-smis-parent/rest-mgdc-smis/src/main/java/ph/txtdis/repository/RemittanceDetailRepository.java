package ph.txtdis.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.RemittanceDetail;

@Repository("remittanceDetailRepository")
public interface RemittanceDetailRepository extends CrudRepository<RemittanceDetail, Long> {

	List<RemittanceDetail> findByBilling(@Param("billing") Billing b);

	// Payments made with post-dated checks, which matured today
	List<RemittanceDetail> //
	findByRemittancePaymentDateLessThanEqualAndRemittanceCheckIdNotNullAndBillingFullyPaidFalseAndBillingUnpaidValue(
			@Param("now") LocalDate d, @Param("zero") BigDecimal u);
}
