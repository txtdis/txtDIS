package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemEntity;

@Repository("itemRepository")
public interface ItemRepository extends SpunRepository<ItemEntity, Long> {

	ItemEntity findByDescription(@Param("description") String d);

	List<ItemEntity> findByDescriptionContaining(@Param("description") String d);

	ItemEntity findByName(@Param("name") String s);

	List<ItemEntity> findByNameStartingWith(@Param("case") String c);

	List<ItemEntity> findByOrderById();

	ItemEntity findByVendorId(@Param("vendorId") String s);
}
