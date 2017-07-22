package ph.txtdis.mgdc.ccbpi.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.PickListDetailEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("pickListDetailRepository")
public interface PickListDetailRepository //
		extends SpunRepository<PickListDetailEntity, Long> {
}
