package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsWeeklyVisit;

@Repository("edmsWeeklyVisitRepository")
public interface EdmsWeeklyVisitRepository
	extends CrudRepository<EdmsWeeklyVisit, Long> {

	EdmsWeeklyVisit findByCode( //
	                            @Param("code") String c);

	EdmsWeeklyVisit findFirstByWeek1sunNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1monNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1tueNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1wedNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1thuNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1friNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek1satNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2sunNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2monNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2tueNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2wedNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2thuNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2friNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek2satNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3sunNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3monNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3tueNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3wedNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3thuNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3friNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek3satNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4sunNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4monNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4tueNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4wedNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4thuNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4friNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);

	EdmsWeeklyVisit findFirstByWeek4satNotOrderByIdDesc( //
	                                                     @Param("sequence") String s);
}
