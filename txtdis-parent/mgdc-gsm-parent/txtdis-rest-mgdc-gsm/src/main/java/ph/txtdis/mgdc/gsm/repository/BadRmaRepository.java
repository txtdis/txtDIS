package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;

@Repository("badRmaRepository")
public interface BadRmaRepository //
		extends RmaRepository {

	List<BillableEntity> findByRmaAndNumIdNotNullAndCustomerId( //
			@Param("rma") boolean b, //
			@Param("customerId") Long id);
}
