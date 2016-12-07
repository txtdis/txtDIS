package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.WarehouseEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("warehouseRepository")
public interface WarehouseRepository extends NameListRepository<WarehouseEntity> {
}
