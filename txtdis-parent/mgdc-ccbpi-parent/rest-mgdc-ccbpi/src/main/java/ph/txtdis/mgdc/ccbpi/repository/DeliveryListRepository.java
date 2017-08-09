package ph.txtdis.mgdc.ccbpi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;

@Repository("deliveryListRepository")
public interface DeliveryListRepository //
	extends CrudRepository<BillableEntity, Long> {

	BillableEntity findFirstByCustomerNullAndOrderDateAndSuffixNotNullAndSuffixContaining( //
	                                                                                       @Param("date") LocalDate
		                                                                                       d, //
	                                                                                       @Param("route") String r);

	List<BillableEntity> findByCustomerNullAndSuffixNotNullAndSuffixContainingAndOrderDateBetween( //
	                                                                                               @Param("route")
		                                                                                               String r, //
	                                                                                               @Param("start")
		                                                                                               LocalDate s, //
	                                                                                               @Param("end")
		                                                                                               LocalDate e);

	BillableEntity findFirstByCustomerNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc( //
	                                                                                    @Param("id") Long id);

	BillableEntity findFirstByCustomerNullAndSuffixNotNullAndIdLessThanOrderByIdDesc( //
	                                                                                  @Param("id") Long id);

	BillableEntity findFirstByCustomerNullAndOrderDateAndSuffixNotNullOrderByIdAsc( //
	                                                                                @Param("date") LocalDate d);

	BillableEntity findFirstByCustomerNullAndSuffixNotNullOrderByIdAsc();

	BillableEntity findFirstByCustomerNullAndSuffixNotNullOrderByIdDesc();
}
