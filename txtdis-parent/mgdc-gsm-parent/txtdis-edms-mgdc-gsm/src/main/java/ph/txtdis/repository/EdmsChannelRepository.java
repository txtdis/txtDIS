package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsOutletType;

@Repository("edmsChannelRepository")
public interface EdmsChannelRepository //
	extends CodeNameRepository<EdmsOutletType> {

	EdmsOutletType findByCodeStartingWith(@Param("code") String c);
}
