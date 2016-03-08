package ph.txtdis.repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Remittance;

@Repository("remittanceRepository")
public interface RemittanceRepository extends SpunRepository<Remittance, Long> {

	List<Remittance> findByDetailsBilling(@Param("billing") Billing b);

	List<Remittance> findByDetailsBillingCustomerNotAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween(
			@Param("vendor") Customer v, @Param("start") LocalDate s, @Param("end") LocalDate e);

	List<Remittance> findByDetailsBillingCustomerNotAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween(
			@Param("vendor") Customer v, @Param("start") LocalDate s, @Param("end") LocalDate e);

	List<Remittance> findByDraweeBankAndCheckId(@Param("bank") Customer b, @Param("checkId") Long id);

	List<Remittance> findByPaymentDateGreaterThanAndDepositedOnNullOrDecidedOnNullOrderByIdDesc(
			@Param("goLive") LocalDate d);

	Remittance findFirstByCollectorAndPaymentDateAndCheckIdNull(@Param("collector") String n,
			@Param("date") LocalDate d);

	Remittance findFirstByCreatedOnBetweenOrderByIdAsc(@Param("start") ZonedDateTime s, @Param("end") ZonedDateTime e);
}
