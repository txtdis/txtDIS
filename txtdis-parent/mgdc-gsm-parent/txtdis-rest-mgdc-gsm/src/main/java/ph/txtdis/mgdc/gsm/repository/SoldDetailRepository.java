package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;

@Repository("soldDetailRepository")
public interface SoldDetailRepository //
		extends CrudRepository<BillableDetailEntity, Long> {
}
