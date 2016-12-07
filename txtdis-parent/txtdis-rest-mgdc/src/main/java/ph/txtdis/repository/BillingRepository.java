package ph.txtdis.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.PartnerType;

@Repository("billingRepository")
public interface BillingRepository extends CrudRepository<BillableEntity, Long> {

	// find P/O
	BillableEntity findByCustomerIdAndBookingId(@Param("vendorId") Long vendorId, @Param("bookingId") Long bookingId);

	// find P/O Receipt
	BillableEntity findByCustomerIdAndReceivingId(@Param("vendorId") Long vendorId,
			@Param("receivingId") Long receivingId);

	// find S/O
	BillableEntity findByCustomerTypeAndBookingIdAndRmaNull(@Param("type") PartnerType type,
			@Param("bookingId") Long bookingId);

	// find S/O Receipt
	BillableEntity findByCustomerIdNotAndReceivingIdAndRmaNull(@Param("vendorId") Long vendorId,
			@Param("receivingId") Long receivingId);

	// S/O with duplicate item for a customer
	BillableEntity findByDetailsItemAndOrderDateAndCustomerIdAndRmaNull(@Param("item") ItemEntity i,
			@Param("orderDate") LocalDate d, @Param("customerId") Long c);

	// find D/R
	BillableEntity findByNumId(@Param("id") Long l);

	BillableEntity findFirstByNumId(@Param("id") long l);

	// S/I between two dates for VAT
	List<BillableEntity> findByNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("zero") Long id, @Param("startOfDay") LocalDate s, @Param("endOfDay") LocalDate e);

	// find S/I for B/O
	List<BillableEntity> findByNumIdGreaterThanAndRmaNullAndCustomerIdOrderByOrderDateDesc(@Param("zero") Long zero,
			@Param("customerId") Long id);

	// aging customer A/R
	List<BillableEntity> findByNumIdNotNullAndFullyPaidFalseAndCustomerIdOrderByOrderDateDesc(
			@Param("customerId") Long id);

	// aging A/R
	List<BillableEntity> findByNumIdNotNullAndFullyPaidFalseOrderByCustomerAscOrderDateDesc();

	// find S/I and D/R for upload
	List<BillableEntity> findByNumIdNotNullAndOrderDateGreaterThanEqualAndUploadedOnNull(@Param("start") LocalDate s);

	// find B/O
	List<BillableEntity> findByNumIdNotNullAndRmaNotNullAndRmaFalseAndCustomerId(@Param("customerId") Long id);

	// find RMA
	List<BillableEntity> findByNumIdNotNullAndRmaNotNullAndRmaTrueAndCustomerId(@Param("customerId") Long id);

	// not fully paid S/I or D/R
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndCustomerIdNotAndFullyPaidFalseAndUnpaidValueGreaterThanAndOrderDateBetweenOrderByOrderDateAsc(
			@Param("customerId") Long id, @Param("zeroUnpaid") BigDecimal u, @Param("start") LocalDate sd,
			@Param("cutoff") LocalDate cd);

	// find S/I and D/R
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndCustomerIdNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("vendorId") Long vendorId, @Param("start") LocalDate s, @Param("end") LocalDate e);

	// unbilled picked S/O
	List<BillableEntity> findByNumIdNullAndRmaNullAndCustomerTypeAndPickingNotNullAndOrderDateBetween(
			@Param("type") PartnerType type, @Param("start") LocalDate s, @Param("end") LocalDate e);

	// unpicked S/O
	List<BillableEntity> findByOrderDateAndCustomerIdNotAndRmaNullAndPickingNull(@Param("date") LocalDate d,
			@Param("vendorId") Long vendorId);

	// find S/I
	BillableEntity findByPrefixAndSuffixAndNumId(@Param("prefix") String p, @Param("suffix") String s,
			@Param("id") Long id);

	// unprinted B/O and RMA
	List<BillableEntity> findByPickingPrintedOnNullAndRmaNotNullAndIsValidTrueAndOrderDateNullAndCustomerIn(
			@Param("customers") List<CustomerEntity> c);

	// find B/O
	BillableEntity findByRmaFalseAndBookingId(@Param("id") Long id);

	// find B/O Receipt
	BillableEntity findByRmaFalseAndReceivingId(@Param("id") Long id);

	// find RMA
	BillableEntity findByRmaTrueAndBookingId(@Param("id") Long id);

	// find RMA Receipt
	BillableEntity findByRmaTrueAndReceivingId(@Param("id") Long id);

	// first P/O of the day
	BillableEntity findFirstByBookingIdNotNullAndCustomerIdAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("vendorId") Long vendorId, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first S/O of the day
	BillableEntity findFirstByBookingIdNotNullAndCustomerTypeAndRmaNullAndOrderDateOrderByBookingIdAsc(
			@Param("type") PartnerType t, @Param("date") LocalDate d);

	// new S/O or P/O
	BillableEntity findFirstByBookingIdNotNullOrderByBookingIdDesc();

	// next P/O
	BillableEntity findFirstByCustomerIdAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("vendorId") Long vendorId, @Param("bookingId") Long bookingId);

	// previous P/O
	BillableEntity findFirstByCustomerIdAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(
			@Param("vendorId") Long vendorId, @Param("bookingId") Long bookingId);

	// first P/O
	BillableEntity findFirstByCustomerIdAndBookingIdNotNullOrderByBookingIdAsc(@Param("vendorId") Long vendorId);

	// last P/O
	BillableEntity findFirstByCustomerIdAndBookingIdNotNullOrderByBookingIdDesc(@Param("vendorId") Long vendorId);

	// first P/O Receipt of the day
	BillableEntity findFirstByCustomerIdAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("vendorId") Long vendorId, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next P/O Receipt
	BillableEntity findFirstByCustomerIdAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("vendorId") Long vendorId, @Param("id") Long id);

	// previous P/O Receipt
	BillableEntity findFirstByCustomerIdAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("vendorId") Long vendorId, @Param("id") Long id);

	// first P/O Receipt
	BillableEntity findFirstByCustomerIdAndReceivingIdNotNullOrderByReceivingIdAsc(@Param("vendorId") Long vendorId);

	// last P/O Receipt
	BillableEntity findFirstByCustomerIdAndReceivingIdNotNullOrderByReceivingIdDesc(@Param("vendorId") Long vendorId);

	// next S/O
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("type") PartnerType type, @Param("id") Long id);

	// previous S/O
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(
			@Param("type") PartnerType type, @Param("id") Long id);

	// first S/O
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullOrderByBookingIdAsc(
			@Param("type") PartnerType type);

	// last S/O
	BillableEntity findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullOrderByBookingIdDesc(
			@Param("type") PartnerType type);

	// first R/R of the day
	BillableEntity findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivedOnBetweenOrderByReceivedOnAsc(
			@Param("vendorId") Long vendorId, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next R/R
	BillableEntity findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("vendorId") Long vendorId, @Param("id") Long id);

	// previous R/R
	BillableEntity findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("vendorId") Long vendorId, @Param("id") Long id);

	// first R/R
	BillableEntity findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdAsc(
			@Param("vendorId") Long vendorId);

	// last R/R
	BillableEntity findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdDesc(
			@Param("vendorId") Long vendorId);

	// first S/I of the day
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanAndOrderDateOrderByIdAsc(@Param("zero") Long z,
			@Param("date") LocalDate d);

	// next S/I (positive numId)
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByIdAsc(@Param("zero") Long z,
			@Param("id") Long i);

	// previous S/I (positive numId)
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdLessThanOrderByIdDesc(@Param("zero") Long z,
			@Param("id") Long i);

	// first S/I (positive numId)
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByNumIdAsc(@Param("zero") Long i);

	// first S/I (last numId)
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByNumIdDesc(@Param("zero") Long i);

	// first D/R of the day
	BillableEntity findFirstByNumIdLessThanAndOrderDateOrderByIdAsc(@Param("zero") Long z, @Param("date") LocalDate d);

	// next D/R
	BillableEntity findFirstByNumIdLessThanAndIdGreaterThanOrderByIdAsc(@Param("zero") Long z, @Param("id") Long id);

	// previous D/R
	BillableEntity findFirstByNumIdLessThanAndIdLessThanOrderByIdDesc(@Param("zero") Long z, @Param("id") Long id);

	// last D/R 
	BillableEntity findFirstByNumIdLessThanOrderByIdDesc(@Param("zero") Long z);

	// first D/R 
	BillableEntity findFirstByNumIdLessThanOrderByIdAsc(@Param("zero") Long z);

	// any S/I by Customer & Item
	BillableEntity findFirstByNumIdNotNullAndRmaNullAndCustomerIdAndOrderDateBetweenAndDetails_Item_Id(
			@Param("customerId") Long c, @Param("start") LocalDate s, @Param("end") LocalDate e, @Param("itemId") Long i);

	// open B/O or RMA
	List<BillableEntity> findByNumIdNullAndRmaNotNullAndCustomerId(@Param("customerId") Long id);

	BillableEntity findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc(@Param("prefix") String p,
			@Param("suffix") String s, @Param("first") Long f, @Param("last") Long l);

	// new Receiving
	BillableEntity findFirstByReceivingIdNotNullOrderByReceivingIdDesc();

	// next B/O
	BillableEntity findFirstByRmaFalseAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("id") Long id);

	// previous B/O
	BillableEntity findFirstByRmaFalseAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(@Param("id") Long id);

	// first B/O of the day
	BillableEntity findFirstByRmaFalseAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first B/O
	BillableEntity findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdAsc();

	// last B/O
	BillableEntity findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdDesc();

	// first B/O Receipt of the day
	BillableEntity findFirstByRmaFalseAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next B/O Receipt
	BillableEntity findFirstByRmaFalseAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("id") Long id);

	// previous B/O Receipt
	BillableEntity findFirstByRmaFalseAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("id") Long id);

	// first B/O Receipt
	BillableEntity findFirstByRmaFalseAndReceivingIdNotNullOrderByReceivingIdAsc();

	// last B/O Receipt
	BillableEntity findFirstByRmaFalseAndReceivingIdNotNullOrderByReceivingIdDesc();

	// next RMA
	BillableEntity findFirstByRmaTrueAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(@Param("id") Long id);

	// previous RMA
	BillableEntity findFirstByRmaTrueAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(@Param("id") Long id);

	// first RMA of the day
	BillableEntity findFirstByRmaTrueAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first RMA
	BillableEntity findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdAsc();

	// last RMA
	BillableEntity findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdDesc();

	// first RMA Receipt of the day
	BillableEntity findFirstByRmaTrueAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next RMA Receipt
	BillableEntity findFirstByRmaTrueAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("id") Long id);

	// previous RMA Receipt
	BillableEntity findFirstByRmaTrueAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("id") Long id);

	// first RMA Receipt
	BillableEntity findFirstByRmaTrueAndReceivingIdNotNullOrderByReceivingIdAsc();

	// last RMA Receipt
	BillableEntity findFirstByRmaTrueAndReceivingIdNotNullOrderByReceivingIdDesc();

	// first exTruck of the day
	BillableEntity findFirstByBookingIdNotNullAndCustomerTypeAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("exTruck") PartnerType t, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next exTruck
	BillableEntity findFirstByCustomerTypeAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("exTruck") PartnerType t, @Param("bookingId") Long id);

	// previous exTruck
	BillableEntity findFirstByCustomerTypeAndBookingIdLessThanOrderByBookingIdDesc(
			@Param("exTruck") PartnerType t, @Param("bookingId") Long id);

	// first exTruck
	BillableEntity findFirstByCustomerTypeAndBookingIdNotNullOrderByBookingIdAsc(@Param("exTruck") PartnerType t);

	// first exTruck
	BillableEntity findFirstByCustomerTypeAndBookingIdNotNullOrderByBookingIdDesc(@Param("exTruck") PartnerType t);

	// booked exTruck
	List<BillableEntity> findByOrderDateAndCustomerType(@Param("orderDate") LocalDate d,
			@Param("exTruck") PartnerType t);

	List<BillableEntity> findByNumIdNotNullAndOrderDateBetweenOrderByOrderDateAsc(@Param("startDate") LocalDate s,
			@Param("endDate") LocalDate e);

	BillableEntity findFirstByCustomerTypeInAndBookingIdAndRmaNullOrderByIdAsc(
			@Param("exTruckAndOutlet") List<PartnerType> t, @Param("bookingId") Long id);

	List<BillableEntity> findByCustomerTypeAndPickingNotNullAndOrderDateBetween(
			@Param("exTruck") PartnerType t, @Param("goLive") LocalDate start, @Param("cutOff") LocalDate end);

	List<BillableEntity> findByOrderDateBetweenAndReceivedOnNotNull(@Param("startDate") LocalDate s,
			@Param("endDate") LocalDate e);

	List<BillableEntity> findByOrderDateBetweenAndPickingNotNull(@Param("startDate") LocalDate s,
			@Param("endDate") LocalDate e);

	// find Delivery Lists of a date range
	List<BillableEntity> findByCustomerNullAndNumIdNotNullAndSuffixNotNullAndOrderDateBetween(
			@Param("start") LocalDate s, @Param("end") LocalDate e);

	// first Delivery List of the day
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndOrderDateOrderByIdAsc(
			@Param("date") LocalDate d);

	// next Delivery List
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc(
			@Param("id") Long id);

	// previous Delivery List
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndIdLessThanOrderByIdDesc(
			@Param("id") Long id);

	// first Delivery List
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullOrderByIdAsc();

	// last Delivery List
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullOrderByIdDesc();

	// find Delivery List
	BillableEntity findByCustomerNullAndNumIdAndSuffix(@Param("shipment") Long id, @Param("route") String r);

	// find Load Manifests of a date range
	List<BillableEntity> findByCustomerNullAndNumIdNotNullAndSuffixNullAndOrderDateBetween(@Param("start") LocalDate s,
			@Param("end") LocalDate e);

	// first Load Manifest of the day
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndOrderDateOrderByIdAsc(
			@Param("date") LocalDate d);

	// next Load Manifest
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndIdGreaterThanOrderByIdAsc(@Param("id") Long id);

	// previous Load Manifest
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndIdLessThanOrderByIdDesc(@Param("id") Long id);

	// first Load Manifest
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNullOrderByIdAsc();

	// last Load Manifest
	BillableEntity findFirstByCustomerNullAndNumIdNotNullAndSuffixNullOrderByIdDesc();

	// find Load Manifest
	BillableEntity findByCustomerNullAndNumIdAndSuffixNull(@Param("shipment") Long id);

	// first Order Confirmation by Booking ID
	BillableEntity findByBookingId(@Param("bookingId") Long id);

	// list Order Confirmation made by Coke's pre-sellers of a date range
	List<BillableEntity> findByCustomerNotNullAndNumIdNotNullAndPrefixNullAndSuffixNotNullAndSuffixNotInAndDueDateBetween(
			@Param("warehouseSalesAndPartial") List<String> l, @Param("start") LocalDate s, @Param("end") LocalDate e);

	// list unpicked Order Confirmation
	List<BillableEntity> findByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndPrefixNullAndSuffixNotInAndDueDateAndPickingNull(
			@Param("warehouseSalesAndPartial") List<String> l, @Param("date") LocalDate d);

	// first Order Confirmation of the day
	BillableEntity findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndOrderDateOrderByIdAsc(
			@Param("date") LocalDate d);

	// find Order Confirmation
	BillableEntity findByCustomerVendorIdAndNumIdAndSuffixNotNullAndOrderDate(@Param("outletId") Long id,
			@Param("orderNo") Long no, @Param("date") LocalDate d);

	// next Order Confirmation
	BillableEntity findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc(
			@Param("id") Long id);

	// previous Order Confirmation
	BillableEntity findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndIdLessThanOrderByIdDesc(
			@Param("id") Long id);

	// first Order Confirmation
	BillableEntity findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullOrderByIdAsc();

	// last Order Confirmation
	BillableEntity findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullOrderByIdDesc();

	// find SIV
	BillableEntity findByNumIdAndRmaNullAndCustomerId(@Param("customerId") Long id, @Param("vendorId") Long vid);

	// find aging receivable after go-live
	List<BillableEntity> findByNumIdNotNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqual(
			@Param("customerId") Long id, @Param("goLive") LocalDate goLive);

	// find past due after go-live
	List<BillableEntity> findByNumIdNotNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqualAndDueDateLessThan(
			@Param("customerId") Long id, @Param("goLive") LocalDate goLive, @Param("now") LocalDate now);

	// find load order
	BillableEntity findByCustomerNameAndOrderDate(@Param("exTruckName") String exTruck, @Param("date") LocalDate d);
}
