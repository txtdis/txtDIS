package ph.txtdis.mgdc.ccbpi.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;

@Repository("orderConfirmationRepository")
public interface OrderConfirmationRepository //
	extends CrudRepository<BillableEntity, Long> {

	BillableEntity findByBookingId( //
	                                @Param("bookingId") Long id);

	List<BillableEntity> findByCustomerNotNull( //
	                                            @Param("type") String t);

	List<BillableEntity> findByCustomerNotNullAndDueDateBetweenAndPickingLeadAssistantNameContaining( //
	                                                                                                  @Param("start")
		                                                                                                  LocalDate s, //
	                                                                                                  @Param("end")
		                                                                                                  LocalDate e, //
	                                                                                                  @Param
		                                                                                                  ("receivedFrom")
		                                                                                                  String c);

	List<BillableEntity> findByCustomerNotNullAndDueDateBetweenAndPickingNull( //
	                                                                           @Param("start") LocalDate s, //
	                                                                           @Param("end") LocalDate e);

	List<BillableEntity> findByCustomerNotNullAndPrefixInAndDueDateAndPickingNull( //
	                                                                               @Param("ocsTypes") List<String> l, //
	                                                                               @Param("date") LocalDate d);

	List<BillableEntity> findByCustomerNotNullAndPrefixAndSuffixContainingAndDueDateBetween( //
	                                                                                         @Param("ocsType") String d,
	                                                                                         //
	                                                                                         @Param("route") String r,
	                                                                                         //
	                                                                                         @Param("start")
		                                                                                         LocalDate s,
	                                                                                         //
	                                                                                         @Param("end") LocalDate e);

	List<BillableEntity> findByCustomerVendorId( //
	                                             @Param("vendorId") Long id);

	BillableEntity findByCustomerVendorIdAndOrderDateAndBookingId( //
	                                                               @Param("outletId") Long id, //
	                                                               @Param("date") LocalDate d, //
	                                                               @Param("orderNo") Long no);

	List<BillableEntity> findByCustomerVendorIdAndPrefixAndOrderDate( //
	                                                                  @Param("partial") String p, //
	                                                                  @Param("outletId") Long id, //
	                                                                  @Param("orderDate") LocalDate d);

	BillableEntity findFirstByCustomerNotNullAndIdGreaterThanOrderByIdAsc( //
	                                                                       @Param("id") Long id);

	BillableEntity findFirstByCustomerNotNullAndIdLessThanOrderByIdDesc( //
	                                                                     @Param("id") Long id);

	BillableEntity findFirstByCustomerNotNullAndOrderDateOrderByIdAsc( //
	                                                                   @Param("date") LocalDate d);

	BillableEntity findFirstByCustomerNotNullOrderByIdAsc();

	BillableEntity findFirstByCustomerNotNullOrderByIdDesc();

	BillableEntity findFirstByCustomerVendorIdAndOrderDateOrderByBookingIdDesc( //
	                                                                            @Param("outletId") Long id, //
	                                                                            @Param("date") LocalDate d);

	BillableEntity findFirstByCustomerVendorIdAndPrefixOrderByOrderDateDesc( //
	                                                                         @Param("blanket") String b, //
	                                                                         @Param("outletId") Long id);

	List<BillableEntity> findByCustomerNotNullAndDueDateBetweenAndPickingLeadAssistantNameContainingAndTotalValueGreaterThan(
		//
		@Param("start") LocalDate s,
		//
		@Param("end") LocalDate e,
		//
		@Param("route") String r,
		//
		@Param("total") BigDecimal o);
}
