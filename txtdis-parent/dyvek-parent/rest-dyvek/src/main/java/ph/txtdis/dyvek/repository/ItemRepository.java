package ph.txtdis.dyvek.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("itemRepository")
public interface ItemRepository //
		extends NameListRepository<ItemEntity> {
}
