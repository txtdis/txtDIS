package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsInventory;

@Repository("edmsInventoryRepository")
public interface EdmsInventoryRepository
	extends CrudRepository<EdmsInventory, Long> {

	EdmsInventory findByItemCodeAndUomCode(@Param("code") String c, @Param("uom") String u);
}
