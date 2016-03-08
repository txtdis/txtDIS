package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Item;

@Repository("itemRepository")
public interface ItemRepository extends SpunRepository<Item, Long> {

	List<Item> findByDescriptionContaining(@Param("description") String d);

	Item findByName(@Param("name") String s);
}
