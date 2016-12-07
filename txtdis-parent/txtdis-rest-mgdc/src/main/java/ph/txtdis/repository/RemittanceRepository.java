package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.RemittanceEntity;

@Repository("remittanceRepository")
public interface RemittanceRepository extends SpunRepository<RemittanceEntity, Long> {

	List<RemittanceEntity> findByDetailsBillingCustomerIdNotAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween(
			@Param("vendorId") Long id, @Param("start") LocalDate s, @Param("end") LocalDate e);

	List<RemittanceEntity> findByDetailsBillingCustomerIdNotAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween(
			@Param("vendorId") Long id, @Param("start") LocalDate s, @Param("end") LocalDate e);

	List<RemittanceEntity> findByDetailsBillingId(@Param("billingId") Long id);

	List<RemittanceEntity> findByDraweeBankIdAndCheckId(@Param("bankId") Long b, @Param("checkId") Long c);

	List<RemittanceEntity> findByPaymentDateBetweenOrderByPaymentDateAsc(@Param("start") LocalDate s,
			@Param("end") LocalDate e);

	List<RemittanceEntity> findByPaymentDateGreaterThanAndDepositedOnNullOrDecidedOnNullOrderByIdDesc(
			@Param("goLive") LocalDate d);

	RemittanceEntity findFirstByCollectorAndPaymentDateAndCheckIdNull(@Param("collector") String n,
			@Param("date") LocalDate d);

	RemittanceEntity findFirstByPaymentDateOrderByIdAsc(@Param("date") LocalDate d);
}
