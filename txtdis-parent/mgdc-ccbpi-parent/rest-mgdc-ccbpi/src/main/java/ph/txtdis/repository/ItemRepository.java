package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Item;

@Repository("itemRepository")
public interface ItemRepository extends NameListRepository<Item> {
}
