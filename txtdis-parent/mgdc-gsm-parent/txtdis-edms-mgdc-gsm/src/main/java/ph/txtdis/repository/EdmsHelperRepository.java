package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsHelper;

@Repository("edmsHelperRepository")
public interface EdmsHelperRepository extends CodeNameRepository<EdmsHelper> {
}
