package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsDriver;

@Repository("edmsDriverRepository")
public interface EdmsDriverRepository extends CodeNameRepository<EdmsDriver> {
}
