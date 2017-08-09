package ph.txtdis.mgdc.ccbpi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("pickListRepository")
public interface PickListRepository //
	extends SpunRepository<PickListEntity, Long> {

	List<PickListEntity> findByPickDate( //
	                                     @Param("date") LocalDate d);

	PickListEntity findByPrintedOnIsNotNullAndBillingsBookingId( //
	                                                             @Param("bookingId") Long id);

	PickListEntity findByReceivedOnNotNullAndId( //
	                                             @Param("id") Long id);

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

	PickListEntity findFirstByPickDateAndTruckId( //
	                                              @Param("date") LocalDate d, //
	                                              @Param("truckId") Long id);

	// next Load Return
	PickListEntity findFirstByReceivedOnNotNullAndIdGreaterThanOrderByIdAsc( //
	                                                                         @Param("id") Long id);

	// previous Load Return
	PickListEntity findFirstByReceivedOnNotNullAndIdLessThanOrderByIdDesc( //
	                                                                       @Param("id") Long id);

	// first Load Return
	PickListEntity findFirstByReceivedOnNotNullOrderByIdAsc();

	// last Load Return
	PickListEntity findFirstByReceivedOnNotNullOrderByIdDesc();
}
