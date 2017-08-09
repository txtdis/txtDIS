package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.gsm.domain.WeeklyVisitEntity;
import ph.txtdis.mgdc.gsm.repository.WeeklyVisitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service("weeklyVisitService")
public class WeeklyVisitServiceImpl
	implements WeeklyVisitService {

	@Autowired
	private WeeklyVisitRepository repository;

	@Override
	public List<WeeklyVisit> toModels(List<WeeklyVisitEntity> l) {
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(Collectors.toList());
	}

	private WeeklyVisit toModel(WeeklyVisitEntity e) {
		WeeklyVisit w = new WeeklyVisit();
		w.setId(e.getId());
		w.setWeekNo(e.getWeekNo());
		w.setSun(toBoolean(e.getSun()));
		w.setMon(toBoolean(e.getMon()));
		w.setTue(toBoolean(e.getTue()));
		w.setWed(toBoolean(e.getWed()));
		w.setThu(toBoolean(e.getThu()));
		w.setFri(toBoolean(e.getFri()));
		w.setSat(toBoolean(e.getSat()));
		return w;
	}

	private boolean toBoolean(Boolean bool) {
		return bool == null ? false : bool;
	}

	@Override
	public List<WeeklyVisitEntity> toEntities(List<WeeklyVisit> l) {
		return l == null ? null : l.stream().map(e -> toEntity(e)).collect(Collectors.toList());
	}

	private WeeklyVisitEntity toEntity(WeeklyVisit t) {
		WeeklyVisitEntity e = findSavedEntity(t);
		if (e == null)
			e = new WeeklyVisitEntity();
		e.setWeekNo(t.getWeekNo());
		e.setSun(toBoolean(t.isSun()));
		e.setMon(toBoolean(t.isMon()));
		e.setTue(toBoolean(t.isTue()));
		e.setWed(toBoolean(t.isWed()));
		e.setThu(toBoolean(t.isThu()));
		e.setFri(toBoolean(t.isFri()));
		e.setSat(toBoolean(t.isSat()));
		return e;
	}

	private WeeklyVisitEntity findSavedEntity(WeeklyVisit t) {
		Long id = t.getId();
		return id == null ? null : repository.findOne(t.getId());
	}

	private Boolean toBoolean(boolean bool) {
		return bool ? true : null;
	}
}