package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.repository.NameListRepository;
import ph.txtdis.type.ItemTier;

@Repository("itemFamilyRepository")
public interface ItemFamilyRepository extends NameListRepository<ItemFamilyEntity> {

	List<ItemFamilyEntity> findByTierOrderByNameAsc(@Param("tier") ItemTier t);
}