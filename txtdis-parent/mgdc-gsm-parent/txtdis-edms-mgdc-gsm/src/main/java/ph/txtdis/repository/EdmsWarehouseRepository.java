package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsWarehouse;

@Repository("edmsWarehouseRepository")
public interface EdmsWarehouseRepository extends CodeNameRepository<EdmsWarehouse> {
}
