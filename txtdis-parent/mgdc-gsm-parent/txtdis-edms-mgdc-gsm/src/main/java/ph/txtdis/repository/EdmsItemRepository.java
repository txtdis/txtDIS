package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsItem;

@Repository("edmsItemRepository")
public interface EdmsItemRepository //
		extends CodeNameRepository<EdmsItem> {

	@Override
	EdmsItem findByCodeIgnoreCase( //
			@Param("code") String c);
}
