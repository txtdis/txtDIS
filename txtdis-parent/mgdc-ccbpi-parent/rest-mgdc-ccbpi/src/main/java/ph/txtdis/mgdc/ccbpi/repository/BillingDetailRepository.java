package ph.txtdis.mgdc.ccbpi.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;

@Repository("billingDetailRepository")
public interface BillingDetailRepository //
		extends CrudRepository<BillableDetailEntity, Long> {

	List<BillableDetailEntity> findByBillingOrderDateGreaterThanEqualOrderByItemAsc( //
			@Param("date") LocalDate d);

	List<BillableDetailEntity> findByItemVendorIdAndBillingCustomerNullAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingOrderDateBetween(
			@Param("itemVendorNo") String id, //
			@Param("route") String r, //`
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<BillableDetailEntity> findByItemVendorIdAndBillingPrefixAndBillingSuffixContainingAndBillingDueDateBetween( //
			@Param("itemVendorNo") String id, //
			@Param("regularType") String t, //
			@Param("route") String r, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	List<BillableDetailEntity> findByItemVendorIdAndReturnedQtyGreaterThanAndBillingPrefixNotNullAndBillingPrefixNotInAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingDueDateBetween( //
			@Param("itemVendorNo") String id, //
			@Param("qtyNotZero") BigDecimal z, //
			@Param("notOcsTypes") List<String> l, //
			@Param("route") String r, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);
}
