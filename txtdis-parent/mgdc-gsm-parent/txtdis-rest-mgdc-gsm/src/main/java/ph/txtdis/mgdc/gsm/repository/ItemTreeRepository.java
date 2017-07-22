package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.ItemTreeEntity;

@Repository("itemTreeRepository")
public interface ItemTreeRepository extends CrudRepository<ItemTreeEntity, Long> {

	ItemTreeEntity findByFamilyId(@Param("familyId") Long id);

	ItemTreeEntity findByFamilyIdAndParentId(@Param("familyId") Long f, @Param("parentId") Long p);

	List<ItemTreeEntity> findByOrderByFamilyAscParentAsc();

	List<ItemTreeEntity> findByParentNameOrderByFamilyNameAsc(@Param("parent") String p);
}