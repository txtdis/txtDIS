package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsItemCategory;

@Repository("edmsItemCategoryRepository")
public interface EdmsItemCategoryRepository extends CodeNameRepository<EdmsItemCategory> {
}
