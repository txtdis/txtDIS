package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.WeeklyVisitEntity;

@Repository("weeklyVisitRepository")
public interface WeeklyVisitRepository extends CrudRepository<WeeklyVisitEntity, Long> {
}
