package ph.txtdis.dyvek.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.repository.SpunRepository;

import java.util.List;

@Repository("clientBillRepository")
public interface ClientBillRepository //
	extends SpunRepository<BillableEntity, Long> {

	BillableEntity findByBillsNotNullAndOrderNo( //
	                                             @Param("billNo") String no);

	List<BillableEntity> findByBillsNotNullAndOrderNoContainingIgnoreCase( //
	                                                                       @Param("billNo") String no);

	BillableEntity findFirstByBillsNotNullAndIdGreaterThanOrderByIdAsc( //
	                                                                    @Param("id") Long id);

	BillableEntity findFirstByBillsNotNullAndIdLessThanOrderByIdDesc( //
	                                                                  @Param("id") Long id);

	BillableEntity findFirstByBillsNotNullOrderByIdAsc();

	BillableEntity findFirstByBillsNotNullOrderByIdDesc();
}
