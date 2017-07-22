package ph.txtdis.mgdc.gsm.repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

@Repository("remittanceRepository")
public interface RemittanceRepository //
		extends SpunRepository<RemittanceEntity, Long> {

	List<RemittanceEntity> findByCollectorContainingAndPaymentDateBetween( //
			@Param("collector") String c, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	RemittanceEntity findByDecidedOnNullAndCheckIdNotNullAndReceivedOnNotNullAndDepositedOnNullAndPaymentDateLessThan( //
			@Param("paymentDate") LocalDate d);

	List<RemittanceEntity> findByDecidedOnNullAndCheckIdNotNullAndReceivedOnNull();

	List<RemittanceEntity> findByDecidedOnNullAndDraweeBankId( //
			@Param("bankId") Long b);

	List<RemittanceEntity> findByDetailsBillingCustomerTypeAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween( ///
			@Param("outletType") PartnerType t, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<RemittanceEntity> findByDetailsBillingCustomerTypeAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween( //
			@Param("outletType") PartnerType t, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<RemittanceEntity> findByDetailsBillingId( //
			@Param("billingId") Long id);

	List<RemittanceEntity> findByDraweeBankName(//
			@Param("bank") String b);

	List<RemittanceEntity> findByDraweeBankNameAndCheckId( //
			@Param("bank") String bank, //
			@Param("checkId") Long c);

	List<RemittanceEntity> findByIsValidNullAndPaymentDateLessThanAndCreatedOnLessThan( //
			@Param("paymentDate") LocalDate p, //
			@Param("createdOn") ZonedDateTime c);

	List<RemittanceEntity> findByPaymentDateBetweenOrderByPaymentDateAsc( //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<RemittanceEntity> findByPaymentDateGreaterThanAndDecidedOnNullOrderByIdDesc( //
			@Param("goLive") LocalDate d);

	RemittanceEntity findFirstByCollectorAndPaymentDateAndCheckIdNull( //
			@Param("collector") String n, //
			@Param("date") LocalDate d);

	RemittanceEntity findFirstByDecidedOnNullAndCheckIdNullAndDepositedOnNullAndPaymentDateLessThan( //
			@Param("paymentDate") LocalDate d);

	RemittanceEntity findFirstByDecidedOnNullAndDepositedOnNotNull();

	RemittanceEntity findFirstByPaymentDateOrderByIdAsc( //
			@Param("paymentDate") LocalDate d);
}
