package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.PickListEntity;
import ph.txtdis.repository.SpunRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository("pickListRepository")
public interface PickListRepository //
	extends SpunRepository<PickListEntity, Long> {

	List<PickListEntity> findByPickDate( //
	                                     @Param("date") LocalDate d);

	PickListEntity findByPrintedOnIsNotNullAndBillingsBookingId( //
	                                                             @Param("bookingId") Long id);

	List<PickListEntity> findDistinctByPickDateBetweenAndLeadAssistantNameContainingAndBillingsNotNull( //
	                                                                                                    @Param("start")
		                                                                                                    LocalDate s,
	                                                                                                    //
	                                                                                                    @Param("end")
		                                                                                                    LocalDate e,
	                                                                                                    //
	                                                                                                    @Param(
		                                                                                                    "receivedFrom")
		                                                                                                    String collector);

	PickListEntity findFirstByBillingsBilledOnNullAndPickDate( //
	                                                           @Param("date") LocalDate d);

	PickListEntity findFirstByBillingsDueDateAndBillingsUnpaidValueGreaterThanAndPickDate( //
	                                                                                       @Param("dueDate")
		                                                                                       LocalDate dd, //
	                                                                                       @Param("unpaid")
		                                                                                       BigDecimal up, //
	                                                                                       @Param("pickDate")
		                                                                                       LocalDate pd);

	PickListEntity findFirstByPickDateAndTruckId( //
	                                              @Param("date") LocalDate d, //
	                                              @Param("truckId") Long id);
}
