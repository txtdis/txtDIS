package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

@Repository("deliveryReportRepository")
public interface DeliveryReportRepository //
	extends CrudRepository<BillableEntity, Long> {

	BillableEntity findByNumId( //
	                            @Param("numId") Long nid);

	BillableEntity findFirstByNumId( //
	                                 @Param("numId") long nid);

	BillableEntity findFirstByNumIdLessThanAndIdGreaterThanOrderByIdAsc( //
	                                                                     @Param("zero") Long z, //
	                                                                     @Param("id") Long id);

	BillableEntity findFirstByNumIdLessThanAndIdLessThanOrderByIdDesc( //
	                                                                   @Param("zero") Long z, //
	                                                                   @Param("id") Long id);

	BillableEntity findFirstByNumIdLessThanOrderByIdAsc( //
	                                                     @Param("zero") Long z);

	BillableEntity findFirstByNumIdLessThanOrderByIdDesc( //
	                                                      @Param("zero") Long z);
}
