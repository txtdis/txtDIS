package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsArea;

@Repository("edmsAreaRepository")
public interface EdmsAreaRepository //
		extends CodeNameRepository<EdmsArea> {
}
