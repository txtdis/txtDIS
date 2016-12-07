package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ScriptEntity;

@Repository("scriptRepository")
public interface ScriptRepository extends CrudRepository<ScriptEntity, Long> {

	List<ScriptEntity> findBySentFalse();

	ScriptEntity findFirstBySentFalse();
}
