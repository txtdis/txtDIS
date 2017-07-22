package ph.txtdis.mgdc.gsm.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

@Repository("billableRepository")
public interface BillableRepository //
		extends SpunRepository<BillableEntity, Long> {

	// S/I between two dates for VAT
	List<BillableEntity> findByCustomerIdNotAndNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("vendorId") Long v, //
			@Param("invoiceNo") Long id, //
			@Param("startOfDay") LocalDate s, //
			@Param("endOfDay") LocalDate e);

	List<BillableEntity> findByCustomerTypeAndBilledOnNotNullAndNumIdGreaterThanAndRmaNullAndPickingNullOrderByOrderDateAscIdAsc( //
			@Param("outletType") PartnerType t, //
			@Param("invoiceNo") Long l);

	List<BillableEntity> findByCustomerTypeAndBilledOnNotNullAndNumIdGreaterThanAndRmaNullOrderByOrderDateAscIdAsc( //
			@Param("outletType") PartnerType t, //
			@Param("invoiceNo") Long l);

	BillableEntity findByCustomerTypeAndBookingId( //
			@Param("partnerType") PartnerType t, //
			@Param("bookingId") Long id);

	List<BillableEntity> findByCustomerTypeAndNumIdNotNullAndRmaNullAndOrderDateBetween( //
			@Param("outletType") PartnerType t, //
			@Param("start") LocalDate s, //
			@Param("cutoff") LocalDate c);

	// not fully paid S/I or D/R
	List<BillableEntity> findByCustomerTypeNotAndNumIdNotNullAndRmaNullAndFullyPaidFalseAndUnpaidValueGreaterThanAndOrderDateBetween(
			@Param("outletType") PartnerType t, //
			@Param("zeroUnpaid") BigDecimal u, //
			@Param("start") LocalDate s, //
			@Param("cutoff") LocalDate c);

	// S/O with duplicate item for a customer
	BillableEntity findByDetailsItemAndOrderDateAndCustomerIdAndRmaNull(//
			@Param("item") ItemEntity i, //
			@Param("orderDate") LocalDate d, //
			@Param("customerId") Long id);

	List<BillableEntity> findByIsValidFalseAndDecidedOnLessThan( //
			@Param("decidedOn") ZonedDateTime d);

	List<BillableEntity> findByIsValidFalseAndNumIdGreaterThanAndDecidedOnLessThan( //
			@Param("invoiceNo") Long id, //
			@Param("decidedOn") ZonedDateTime d);

	List<BillableEntity> findByIsValidFalseAndNumIdLessThanAndDecidedOnLessThan( //
			@Param("drNo") Long id, //
			@Param("decidedOn") ZonedDateTime d);

	// find S/I for B/O
	List<BillableEntity> findByNumIdGreaterThanAndRmaNullAndCustomerIdOrderByOrderDateDesc( //
			@Param("invoiceNo") Long nid, //
			@Param("customerId") Long cid);

	// invalid invoices
	List<BillableEntity> findByNumIdInAndIsValidNull(List<Long> ids);

	// aging A/R
	List<BillableEntity> findByNumIdNotNullAndFullyPaidFalseOrderByCustomerAscOrderDateDesc();

	// find S/I and D/R
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndCustomerIdNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
			@Param("vendorId") Long vid, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	// find aging receivable after go-live
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqual( //
			@Param("customerId") Long id, //
			@Param("goLive") LocalDate d);

	// find past due after go-live
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqualAndDueDateLessThan(
			@Param("customerId") Long id, //
			@Param("goLive") LocalDate d, //
			@Param("now") LocalDate now);

	// aging customer A/R
	List<BillableEntity> findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdOrderByOrderDateDesc( //
			@Param("customerId") Long id);

	// all picked
	List<BillableEntity> findByOrderDateBetweenAndPickingNotNull( //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	// all incoming
	List<BillableEntity> findByOrderDateBetweenAndReceivedOnNotNull( //
			@Param("startDate") LocalDate s, //
			@Param("endDate") LocalDate e);

	// all unprinted RMA
	List<BillableEntity> findByPickingPrintedOnNullAndRmaNotNullAndIsValidTrueAndOrderDateNullAndCustomerIn(
			@Param("customers") List<CustomerEntity> c);

	// find S/I
	BillableEntity findByPrefixAndSuffixAndNumId( //
			@Param("prefix") String p, //
			@Param("suffix") String s, //
			@Param("id") Long id);

	// next S/I
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByIdAsc( //
			@Param("invoiceNo") Long z, //
			@Param("id") Long i);

	// previous S/I
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdLessThanOrderByIdDesc( //
			@Param("invoiceNo") Long z, //
			@Param("id") Long i);

	// first S/I
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByIdAsc( //
			@Param("invoiceNo") Long i);

	// last S/I
	BillableEntity findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByIdDesc( //
			@Param("invoiceNo") Long i);

	BillableEntity findFirstByBookingId(Long id);

	// last booking id
	BillableEntity findFirstByBookingIdNotNullOrderByBookingIdDesc();

	BillableEntity findFirstByNumIdLessThanOrderByNumIdAsc( //
			@Param("zero") Long z);

	// any S/I by Customer & Item
	BillableEntity findFirstByNumIdNotNullAndRmaNullAndCustomerIdAndOrderDateBetweenAndDetails_Item_IdOrderByOrderDateDesc( //
			@Param("customerId") Long c, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e, //
			@Param("itemId") Long i);

	// lastest
	BillableEntity findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc( //
			@Param("prefix") String p, //
			@Param("suffix") String s, //
			@Param("first") Long f, //
			@Param("last") Long l);

	// last receiving id
	BillableEntity findFirstByReceivingIdNotNullOrderByReceivingIdDesc();
}
