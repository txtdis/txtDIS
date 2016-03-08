package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Warehouse;

@Repository("warehouseRepository")
public interface WarehouseRepository extends NameListRepository<Warehouse> {
}
