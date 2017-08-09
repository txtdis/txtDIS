package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.repository.NameListRepository;
import ph.txtdis.type.ItemTier;

import java.util.List;

@Repository("itemFamilyRepository")
public interface ItemFamilyRepository
	extends NameListRepository<ItemFamilyEntity> {

	List<ItemFamilyEntity> findByTierOrderByNameAsc(@Param("tier") ItemTier t);
}