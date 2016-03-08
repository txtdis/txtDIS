package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemFamily;
import ph.txtdis.domain.ItemTree;

@Repository("itemTreeRepository")
public interface ItemTreeRepository extends CrudRepository<ItemTree, Long> {

	ItemTree findByFamily(@Param("family") ItemFamily family);

	ItemTree findByFamilyAndParent(@Param("family") ItemFamily family, @Param("parent") ItemFamily parent);

	List<ItemTree> findByOrderByFamilyAscParentAsc();
}