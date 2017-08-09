package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.HolidayEntity;

import java.time.LocalDate;
import java.util.List;

@Repository("holidayRepository")
public interface HolidayRepository
	extends CrudRepository<HolidayEntity, Long> {

	HolidayEntity findByDeclaredDate(@Param("date") LocalDate d);

	List<HolidayEntity> findByOrderByDeclaredDateDesc();
}
