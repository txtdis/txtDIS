package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Holiday;

@Repository("holidayRepository")
public interface HolidayRepository extends CrudRepository<Holiday, Long> {

	Holiday findByDeclaredDate(@Param("date") LocalDate d);

	List<Holiday> findByOrderByDeclaredDateDesc();
}
