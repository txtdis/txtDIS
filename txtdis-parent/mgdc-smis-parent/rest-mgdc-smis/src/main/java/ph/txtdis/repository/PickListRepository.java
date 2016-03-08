package ph.txtdis.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Picking;

@Repository("pickingRepository")
public interface PickListRepository extends SpunRepository<Picking, Long> {

	List<Picking> findByPickDate(@Param("date") LocalDate d);

	Picking findByPrintedOnIsNotNullAndBillingsBookingId(@Param("bookingId") Long id);

	Picking findFirstByBillingsBilledOnNullAndPickDate(@Param("date") LocalDate d);

	Picking findFirstByBillingsDueDateAndBillingsUnpaidValueGreaterThanAndPickDate(@Param("dueDate") LocalDate dd,
			@Param("unpaid") BigDecimal up, @Param("pickDate") LocalDate pd);
}
