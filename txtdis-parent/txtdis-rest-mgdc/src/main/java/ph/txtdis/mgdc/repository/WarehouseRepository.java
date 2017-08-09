package ph.txtdis.mgdc.repository;

import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.WarehouseEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("warehouseRepository")
public interface WarehouseRepository
	extends NameListRepository<WarehouseEntity> {
}
