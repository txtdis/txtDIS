package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemFamily;
import ph.txtdis.type.ItemTier;

@Repository("itemFamilyRepository")
public interface ItemFamilyRepository extends NameListRepository<ItemFamily> {

	List<ItemFamily> findByTierOrderByNameAsc(@Param("tier") ItemTier t);
}