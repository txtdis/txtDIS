package ph.txtdis.repository;

import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsTerritory;

@Repository("edmsTerritoryRepository")
public interface EdmsTerritoryRepository
	extends CodeNameRepository<EdmsTerritory> {
}
