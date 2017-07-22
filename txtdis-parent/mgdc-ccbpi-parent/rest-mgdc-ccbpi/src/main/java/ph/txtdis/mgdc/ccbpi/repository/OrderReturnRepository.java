package ph.txtdis.mgdc.ccbpi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;

@Repository("orderReturnRepository")
public interface OrderReturnRepository //
		extends CrudRepository<BillableEntity, Long> {

	List<BillableEntity> findByCustomerNotNullAndPrefixAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNull( //
			@Param("ocsType") String t);

	List<BillableEntity> findByCustomerNotNullAndPrefixNotNullAndPrefixNotInAndSuffixNotNullAndBookingIdNotNullAndDueDateBetweenAndReceivedOnNotNull(
			@Param("notOcsTypes") List<String> l, // 
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	BillableEntity findByCustomerVendorIdAndBookingIdAndSuffixNotNullAndOrderDateAndReceivedOnNotNull( //
			@Param("outletId") Long id, //
			@Param("orderSequence") Long no, //
			@Param("date") LocalDate d);

	BillableEntity findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullAndIdGreaterThanOrderByIdAsc(
			@Param("id") Long id);

	BillableEntity findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullAndIdLessThanOrderByIdDesc(
			@Param("id") Long id);

	BillableEntity findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullOrderByIdAsc();

	BillableEntity findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullOrderByIdDesc();
}
