package ph.txtdis.mgdc.gsm.repository;

import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.PickListDetailEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("pickListDetailRepository")
public interface PickListDetailRepository
	extends SpunRepository<PickListDetailEntity, Long> {
}
