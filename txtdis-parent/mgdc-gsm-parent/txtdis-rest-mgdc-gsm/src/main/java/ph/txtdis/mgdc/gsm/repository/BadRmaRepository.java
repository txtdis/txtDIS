package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

import java.util.List;

@Repository("badRmaRepository")
public interface BadRmaRepository //
	extends RmaRepository {

	List<BillableEntity> findByRmaAndNumIdNotNullAndCustomerId( //
	                                                            @Param("rma") boolean b, //
	                                                            @Param("customerId") Long id);
}
