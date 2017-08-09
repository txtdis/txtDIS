package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository("billingDetailRepository")
public interface BillingDetailRepository //
	extends CrudRepository<BillableDetailEntity, Long> {

	List<BillableDetailEntity> findByBilling( //
	                                          @Param("billing") BillableEntity b);

	List<BillableDetailEntity> findByBillingOrderDateGreaterThanEqualOrderByItemAsc( //
	                                                                                 @Param("date") LocalDate d);

	List<BillableDetailEntity>
	findByItemVendorIdAndBillingCustomerNullAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingOrderDateBetween(
		@Param("itemVendorNo") String id, //
		@Param("route") String r, //`
		@Param("start") LocalDate s, //
		@Param("end") LocalDate e);

	List<BillableDetailEntity> findByItemVendorIdAndBillingPrefixAndBillingSuffixContainingAndBillingDueDateBetween( //
	                                                                                                                 @Param(
		                                                                                                                 "itemVendorNo")
		                                                                                                                 String id,

	                                                                                                                 //
	                                                                                                                 @Param(
		                                                                                                                 "regularType")
		                                                                                                                 String t,
	                                                                                                                 //
	                                                                                                                 @Param(
		                                                                                                                 "route")
		                                                                                                                 String r,
	                                                                                                                 //
	                                                                                                                 @Param(
		                                                                                                                 "start")
		                                                                                                                 LocalDate s,
	                                                                                                                 //
	                                                                                                                 @Param(
		                                                                                                                 "end")
		                                                                                                                 LocalDate e);

	List<BillableDetailEntity> findByItemVendorIdAndReturnedQtyGreaterThanAndBillingPrefixNotNullAndBillingPrefixNotInAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingDueDateBetween(
		//
		@Param("itemVendorNo") String id,
		//
		@Param("qtyNotZero") BigDecimal z,
		//
		@Param("notOcsTypes") List<String> l,
		//
		@Param("route") String r,
		//
		@Param("start") LocalDate s,
		//
		@Param("end") LocalDate e);

	List<BillableDetailEntity> findByBillingId( //
	                                            @Param("billingId") Long id);
}
