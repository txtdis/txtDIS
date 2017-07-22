package ph.txtdis.dyvek.repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("remittanceRepository")
public interface RemittanceRepository //
		extends SpunRepository<RemittanceEntity, Long> {

	List<RemittanceEntity> findByDecidedOnNullAndDrawnFromId(//
			@Param("bankId") Long b);

	List<RemittanceEntity> findByDecidedOnNullAndPaymentDateLessThanAndCreatedOnLessThan( //
			@Param("paymentDate") LocalDate p, //
			@Param("createdOn") ZonedDateTime c);

	List<RemittanceEntity> findByDetailsBillingId( //
			@Param("billingId") Long id);

	List<RemittanceEntity> findByDrawnFromId(//
			@Param("bankId") Long b);

	List<RemittanceEntity> findByDrawnFromNameAndCheckId( //
			@Param("bank") String b, //
			@Param("checkId") Long c);

	List<RemittanceEntity> findByPaymentDateBetweenOrderByPaymentDateAsc( //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<RemittanceEntity> findByPaymentDateGreaterThanAndDecidedOnNullOrderByIdDesc( //
			@Param("goLive") LocalDate d);

	RemittanceEntity findFirstByDetailsBillingId( //
			@Param("billingId") Long id);

	RemittanceEntity findFirstByPaymentDateOrderByIdAsc( //
			@Param("date") LocalDate d);
}
