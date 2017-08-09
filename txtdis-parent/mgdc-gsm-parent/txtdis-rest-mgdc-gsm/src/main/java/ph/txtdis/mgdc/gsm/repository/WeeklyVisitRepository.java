package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.WeeklyVisitEntity;

@Repository("weeklyVisitRepository")
public interface WeeklyVisitRepository
	extends CrudRepository<WeeklyVisitEntity, Long> {
}
