package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.WeeklyVisitEntity;
import ph.txtdis.dto.WeeklyVisit;

public interface WeeklyVisitService {

	List<WeeklyVisit> toList(List<WeeklyVisitEntity> l);

	List<WeeklyVisitEntity> toEntities(List<WeeklyVisit> l);

}