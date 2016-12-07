package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsWeeklyVisit;

@Repository("edmsWeeklyVisitRepository")
public interface EdmsWeeklyVisitRepository extends CrudRepository<EdmsWeeklyVisit, Long> {

	EdmsWeeklyVisit findByCode(@Param("code") String code);
}
