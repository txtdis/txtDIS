package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.gsm.domain.WeeklyVisitEntity;

import java.util.List;

public interface WeeklyVisitService {

	List<WeeklyVisit> toModels(List<WeeklyVisitEntity> l);

	List<WeeklyVisitEntity> toEntities(List<WeeklyVisit> l);
}