package ph.txtdis.repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Item;

@Repository("billingRepository")
public interface BillingRepository extends CrudRepository<Billing, Long> {

	Billing findByBookingId(@Param("id") Long id);

	// find P/O
	Billing findByCustomerAndBookingId(@Param("vendor") Customer c, @Param("id") Long id);

	// find P/O Receipt
	Billing findByCustomerAndReceivingId(@Param("vendor") Customer c, @Param("id") Long id);

	// find S/O
	Billing findByCustomerNotAndBookingIdAndRmaNull(@Param("vendor") Customer c, @Param("id") Long id);

	// find S/O Receipt
	Billing findByCustomerNotAndReceivingIdAndRmaNull(@Param("vendor") Customer c, @Param("id") Long id);

	// S/O with duplicate item for a customer
	Billing findByDetailsItemAndOrderDateAndCustomerAndRmaNull(@Param("item") Item i, @Param("orderDate") LocalDate d,
			@Param("customer") Customer c);

	// find D/R
	Billing findByNumId(@Param("id") long l);

	// S/I between two dates for VAT
	List<Billing> findByNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("zero") Long id, @Param("startOfDay") LocalDate s, @Param("endOfDay") LocalDate e);

	// find S/I for B/O
	List<Billing> findByNumIdGreaterThanAndRmaNullAndCustomerOrderByOrderDateDesc(@Param("zero") Long id,
			@Param("customer") Customer c);

	// aging customer A/R
	List<Billing> findByNumIdNotNullAndFullyPaidFalseAndCustomerOrderByOrderDateDesc(@Param("customer") Customer c);

	// aging A/R
	List<Billing> findByNumIdNotNullAndFullyPaidFalseOrderByCustomerAscOrderDateDesc();

	// find S/I and D/R for upload
	List<Billing> findByNumIdNotNullAndOrderDateGreaterThanEqualAndUploadedOnNull(@Param("start") LocalDate s);

	// find B/O
	List<Billing> findByNumIdNotNullAndRmaNotNullAndRmaFalseAndCustomer(@Param("customer") Customer c);

	// find RMA
	List<Billing> findByNumIdNotNullAndRmaNotNullAndRmaTrueAndCustomer(@Param("customer") Customer c);

	// not fully paid S/I or D/R
	List<Billing> findByNumIdNotNullAndRmaNullAndCustomerNotAndFullyPaidFalseAndOrderDateBetweenOrderByOrderDateAsc(
			@Param("customer") Customer c, @Param("start") LocalDate sd, @Param("cutoff") LocalDate cd);

	// find S/I and D/R
	List<Billing> findByNumIdNotNullAndRmaNullAndCustomerNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("vendor") Customer c, @Param("start") LocalDate s, @Param("end") LocalDate e);

	// unbilled picked S/O
	List<Billing> findByNumIdNullAndRmaNullAndCustomerNotAndBookingIdNotNullAndPickingNotNullAndOrderDateBetweenOrderByOrderDateAsc(
			@Param("vendor") Customer c, @Param("start") LocalDate s, @Param("end") LocalDate e);

	// unpicked S/O
	List<Billing> findByOrderDateAndCustomerNotAndRmaNullAndPickingNull(@Param("date") LocalDate d,
			@Param("vendor") Customer c);

	// find S/I
	Billing findByPrefixAndSuffixAndNumId(@Param("prefix") String p, @Param("suffix") String s, @Param("id") Long id);

	// unprinted B/O and RMA
	List<Billing> findByPrintedOnNullAndRmaNotNullAndIsValidTrueAndOrderDateNullAndCustomerIn(
			@Param("customers") List<Customer> c);

	// find B/O
	Billing findByRmaFalseAndBookingId(@Param("id") Long id);

	// find B/O Receipt
	Billing findByRmaFalseAndReceivingId(@Param("id") Long id);

	// find RMA
	Billing findByRmaTrueAndBookingId(@Param("id") Long id);

	// find RMA Receipt
	Billing findByRmaTrueAndReceivingId(@Param("id") Long id);

	// first P/O of the day
	Billing findFirstByBookingIdNotNullAndCustomerAndCreatedOnBetweenOrderByCreatedOnAsc(@Param("vendor") Customer c,
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first S/O of the day
	Billing findFirstByBookingIdNotNullAndCustomerNotAndRmaNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("vendor") Customer c, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// new S/O or P/O
	Billing findFirstByBookingIdNotNullOrderByBookingIdDesc();

	// next P/O
	Billing findFirstByCustomerAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// previous P/O
	Billing findFirstByCustomerAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(@Param("vendor") Customer c,
			@Param("id") Long id);

	// first P/O
	Billing findFirstByCustomerAndBookingIdNotNullOrderByBookingIdAsc(@Param("vendor") Customer c);

	// last P/O
	Billing findFirstByCustomerAndBookingIdNotNullOrderByBookingIdDesc(@Param("vendor") Customer c);

	// first P/O Receipt of the day
	Billing findFirstByCustomerAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(@Param("vendor") Customer c,
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next P/O Receipt
	Billing findFirstByCustomerAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// previous P/O Receipt
	Billing findFirstByCustomerAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// first P/O Receipt
	Billing findFirstByCustomerAndReceivingIdNotNullOrderByReceivingIdAsc(@Param("vendor") Customer c);

	// last P/O Receipt
	Billing findFirstByCustomerAndReceivingIdNotNullOrderByReceivingIdDesc(@Param("vendor") Customer c);

	// next S/O
	Billing findFirstByCustomerNotAndRmaNullAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// previous S/O
	Billing findFirstByCustomerNotAndRmaNullAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// first S/O
	Billing findFirstByCustomerNotAndRmaNullAndBookingIdNotNullOrderByBookingIdAsc(@Param("vendor") Customer c);

	// last S/O
	Billing findFirstByCustomerNotAndRmaNullAndBookingIdNotNullOrderByBookingIdDesc(@Param("vendor") Customer c);

	// first R/R of the day
	Billing findFirstByCustomerNotAndRmaNullAndReceivingIdNotNullAndReceivedOnBetweenOrderByReceivedOnAsc(
			@Param("vendor") Customer c, @Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next R/R
	Billing findFirstByCustomerNotAndRmaNullAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// previous R/R
	Billing findFirstByCustomerNotAndRmaNullAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
			@Param("vendor") Customer c, @Param("id") Long id);

	// first R/R
	Billing findFirstByCustomerNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdAsc(@Param("vendor") Customer c);

	// last R/R
	Billing findFirstByCustomerNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdDesc(@Param("vendor") Customer c);

	// first S/I of the day
	Billing findFirstByNumIdNotNullAndNumIdGreaterThanAndCreatedOnBetweenOrderByCreatedOnAsc(@Param("zero") Long z,
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next S/I (positive numId)
	Billing findFirstByNumIdNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByNumIdAsc(@Param("zero") Long z,
			@Param("id") Long i);

	// previous S/I (positive numId)
	Billing findFirstByNumIdNotNullAndNumIdGreaterThanAndIdLessThanOrderByNumIdDesc(@Param("zero") Long z,
			@Param("id") Long i);

	// first S/I (positive numId)
	Billing findFirstByNumIdNotNullAndNumIdGreaterThanOrderByNumIdAsc(@Param("zero") Long i);

	// first S/I (last numId)
	Billing findFirstByNumIdNotNullAndNumIdGreaterThanOrderByNumIdDesc(@Param("zero") Long i);

	// first D/R of the day
	Billing findFirstByNumIdNotNullAndNumIdLessThanAndCreatedOnBetweenOrderByCreatedOnAsc(@Param("zero") Long z,
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// previous D/R (negative numId)
	Billing findFirstByNumIdNotNullAndNumIdLessThanAndNumIdGreaterThanOrderByNumIdAsc(@Param("zero") Long z,
			@Param("id") Long i);

	// next D/R (negative numId)
	Billing findFirstByNumIdNotNullAndNumIdLessThanAndNumIdLessThanOrderByNumIdDesc(@Param("zero") Long z,
			@Param("id") Long i);

	// last D/R (negative numId)
	Billing findFirstByNumIdNotNullAndNumIdLessThanOrderByNumIdAsc(@Param("zero") Long z);

	// first D/R (negative numId)
	Billing findFirstByNumIdNotNullAndNumIdLessThanOrderByNumIdDesc(@Param("zero") Long z);

	// any S/I by Customer & Item
	Billing findFirstByNumIdNotNullAndRmaNullAndCustomerAndOrderDateBetweenAndDetailsItem(@Param("customer") Customer c,
			@Param("start") LocalDate s, @Param("end") LocalDate e, @Param("item") Item i);

	// open B/O or RMA
	Billing findFirstByNumIdNullAndRmaNotNullAndCustomer(@Param("customer") Customer c);

	Billing findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc(@Param("prefix") String p,
			@Param("suffix") String s, @Param("first") Long f, @Param("last") Long l);

	// new Receiving
	Billing findFirstByReceivingIdNotNullOrderByReceivingIdDesc();

	// next B/O
	Billing findFirstByRmaFalseAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(@Param("id") Long id);

	// previous B/O
	Billing findFirstByRmaFalseAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(@Param("id") Long id);

	// first B/O of the day
	Billing findFirstByRmaFalseAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first B/O
	Billing findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdAsc();

	// last B/O
	Billing findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdDesc();

	// first B/O Receipt of the day
	Billing findFirstByRmaFalseAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next B/O Receipt
	Billing findFirstByRmaFalseAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
			@Param("id") Long id);

	// previous B/O Receipt
	Billing findFirstByRmaFalseAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(@Param("id") Long id);

	// first B/O Receipt
	Billing findFirstByRmaFalseAndReceivingIdNotNullOrderByReceivingIdAsc();

	// last B/O Receipt
	Billing findFirstByRmaFalseAndReceivingIdNotNullOrderByReceivingIdDesc();

	// next RMA
	Billing findFirstByRmaTrueAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(@Param("id") Long id);

	// previous RMA
	Billing findFirstByRmaTrueAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(@Param("id") Long id);

	// first RMA of the day
	Billing findFirstByRmaTrueAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// first RMA
	Billing findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdAsc();

	// last RMA
	Billing findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdDesc();

	// first RMA Receipt of the day
	Billing findFirstByRmaTrueAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
			@Param("startOfDay") ZonedDateTime s, @Param("endOfDay") ZonedDateTime e);

	// next RMA Receipt
	Billing findFirstByRmaTrueAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(@Param("id") Long id);

	// previous RMA Receipt
	Billing findFirstByRmaTrueAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(@Param("id") Long id);

	// first RMA Receipt
	Billing findFirstByRmaTrueAndReceivingIdNotNullOrderByReceivingIdAsc();

	// last RMA Receipt
	Billing findFirstByRmaTrueAndReceivingIdNotNullOrderByReceivingIdDesc();
}
