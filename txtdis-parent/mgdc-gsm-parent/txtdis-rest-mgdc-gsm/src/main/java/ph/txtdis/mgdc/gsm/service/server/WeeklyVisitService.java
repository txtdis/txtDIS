package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.gsm.domain.WeeklyVisitEntity;

public interface WeeklyVisitService {

	List<WeeklyVisit> toModels(List<WeeklyVisitEntity> l);

	List<WeeklyVisitEntity> toEntities(List<WeeklyVisit> l);
}