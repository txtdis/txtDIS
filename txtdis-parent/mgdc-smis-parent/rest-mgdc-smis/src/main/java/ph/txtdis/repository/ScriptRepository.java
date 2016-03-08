package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Script;

@Repository("scriptRepository")
public interface ScriptRepository extends CrudRepository<Script, Long> {

	List<Script> findBySentFalse();

	Script findFirstBySentFalse();
}
