package ph.txtdis.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.PickListEntity;

@Repository("pickingRepository")
public interface PickListRepository extends SpunRepository<PickListEntity, Long> {

	List<PickListEntity> findByPickDate(@Param("date") LocalDate d);

	PickListEntity findByPrintedOnIsNotNullAndBillingsBookingId(@Param("bookingId") Long id);

	PickListEntity findFirstByBillingsBilledOnNullAndPickDate(@Param("date") LocalDate d);

	PickListEntity findFirstByBillingsDueDateAndBillingsUnpaidValueGreaterThanAndPickDate(@Param("dueDate") LocalDate dd,
			@Param("unpaid") BigDecimal up, @Param("pickDate") LocalDate pd);

	PickListEntity findFirstByPickDateAndTruckId(@Param("pickDate") LocalDate d, @Param("truckId") Long id);

	// find Load Return
	PickListEntity findByIdAndReceivedOnNotNull(Long id);

	// first Load Return
	PickListEntity findFirstByReceivedOnNotNullOrderByIdAsc();

	// last Load Return
	PickListEntity findFirstByReceivedOnNotNullOrderByIdDesc();

	// next Load Return
	PickListEntity findFirstByReceivedOnNotNullAndIdGreaterThanOrderByIdAsc(Long id);

	// previous Load Return
	PickListEntity findFirstByReceivedOnNotNullAndIdLessThanOrderByIdDesc(Long id);
}
