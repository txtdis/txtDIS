package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface BookingRepository //
	extends SpunRepository<BillableEntity, Long> {

	// a booking w/ returns  
	BillableEntity findByCustomerTypeAndBookingId( //
	                                               @Param("exTruck") PartnerType t, //
	                                               @Param("loadOrderId") Long id);

	// find by booking id
	BillableEntity findFirstByCustomerTypeInAndBookingIdAndRmaNullOrderById(//
	                                                                        @Param("types") List<PartnerType> l, //
	                                                                        @Param("bookingId") Long id);

	// all booked on a date
	List<BillableEntity> findByOrderDateAndCustomerType( //
	                                                     @Param("orderDate") LocalDate d, //
	                                                     @Param("exTruck") PartnerType t);

	// unpicked bookings on a date
	List<BillableEntity> findByOrderDateAndCustomerTypeInAndBilledOnNullAndReceivedOnNullAndRmaNullAndPickingNull( //
	                                                                                                               @Param(
		                                                                                                               "orderDate")
		                                                                                                               LocalDate d,

	                                                                                                               //
	                                                                                                               @Param(
		                                                                                                               "partnerType")
		                                                                                                               List<PartnerType> t);

	// all picked bookings between dates
	List<BillableEntity> findByOrderDateBetweenAndPickingNotNull( //
	                                                              @Param("start") LocalDate s, //
	                                                              @Param("end") LocalDate e);

	// all booking returns between dates
	List<BillableEntity> findByOrderDateBetweenAndReceivedOnNotNull( //
	                                                                 @Param("startDate") LocalDate s, //
	                                                                 @Param("endDate") LocalDate e);

	// last booking for id determination
	BillableEntity findFirstByBookingIdNotNullOrderByBookingIdDesc();

	// next booking
	BillableEntity findFirstByCustomerTypeAndRmaNullAndIdGreaterThanAndBookingIdGreaterThanOrderByIdAsc(//
	                                                                                                    @Param(
		                                                                                                    "partnerType")
		                                                                                                    PartnerType t,
	                                                                                                    //
	                                                                                                    @Param("id")
		                                                                                                    Long id,
	                                                                                                    //
	                                                                                                    @Param(
		                                                                                                    "zeroBookingId")
		                                                                                                    Long l);

	// previous booking
	BillableEntity findFirstByCustomerTypeAndRmaNullAndIdLessThanAndBookingIdGreaterThanOrderByIdDesc(//
	                                                                                                  @Param(
		                                                                                                  "partnerType")
		                                                                                                  PartnerType t,
	                                                                                                  //
	                                                                                                  @Param("id")
		                                                                                                  Long id,
	                                                                                                  //
	                                                                                                  @Param(
		                                                                                                  "zeroBookingId")
		                                                                                                  Long l);

	// first booking
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdAsc( //
	                                                                                     @Param("partnerType")
		                                                                                     PartnerType t, //
	                                                                                     @Param("zeroBookingId") Long l);

	// last booking
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdDesc( //
	                                                                                      @Param("partnerType")
		                                                                                      PartnerType t, //
	                                                                                      @Param("zeroBookingId")
		                                                                                      Long l);
}