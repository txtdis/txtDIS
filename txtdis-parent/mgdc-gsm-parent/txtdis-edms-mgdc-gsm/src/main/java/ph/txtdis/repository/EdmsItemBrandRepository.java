package ph.txtdis.repository;

import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsItemBrand;

@Repository("edmsItemBrandRepository")
public interface EdmsItemBrandRepository
	extends CodeNameRepository<EdmsItemBrand> {
}
