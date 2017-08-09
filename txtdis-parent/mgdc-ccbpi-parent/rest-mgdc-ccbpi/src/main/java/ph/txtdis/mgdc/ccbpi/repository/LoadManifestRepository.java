package ph.txtdis.mgdc.ccbpi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;

@Repository("loadManifestRepository")
public interface LoadManifestRepository //
	extends CrudRepository<BillableEntity, Long> {

	BillableEntity findByCustomerNullAndBookingIdAndSuffixNull( //
	                                                            @Param("shipment") Long id);

	List<BillableEntity> findByCustomerNullAndBookingIdNotNullAndSuffixNullAndOrderDateBetween( //
	                                                                                            @Param("start")
		                                                                                            LocalDate s, //
	                                                                                            @Param("end")
		                                                                                            LocalDate e);

	BillableEntity findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullAndIdGreaterThanOrderByIdAsc( //
	                                                                                                    @Param("id")
		                                                                                                    Long id);

	BillableEntity findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullAndIdLessThanOrderByIdDesc( //
	                                                                                                  @Param("id")
		                                                                                                  Long id);

	BillableEntity findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullAndOrderDateOrderByIdAsc( //
	                                                                                                @Param("date")
		                                                                                                LocalDate d);

	BillableEntity findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullOrderByIdAsc();

	BillableEntity findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullOrderByIdDesc();
}
