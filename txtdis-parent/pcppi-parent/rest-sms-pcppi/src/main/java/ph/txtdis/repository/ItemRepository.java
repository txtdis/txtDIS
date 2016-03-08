package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Item;

@Repository("itemRepository")
public interface ItemRepository extends SpunRepository<Item, Long> {

	Item findBySmsId(@Param("sms") String id);
}
