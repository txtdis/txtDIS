package ph.txtdis.mgdc.service.server;

import static java.time.DayOfWeek.SUNDAY;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Holiday;
import ph.txtdis.mgdc.domain.HolidayEntity;
import ph.txtdis.mgdc.repository.HolidayRepository;
import ph.txtdis.service.AbstractSavedReferencedKeyedService;

@Service("holidayService")
public class HolidayServiceImpl //
		extends AbstractSavedReferencedKeyedService<HolidayRepository, HolidayEntity, Holiday, Long> //
		implements HolidayService {

	@Override
	public Holiday findByDate(LocalDate d) {
		HolidayEntity e = repository.findByDeclaredDate(d);
		return e == null ? null : toModel(e);
	}

	@Override
	public List<Holiday> list() {
		List<HolidayEntity> l = repository.findByOrderByDeclaredDateDesc();
		return convert(l);
	}

	private List<Holiday> convert(List<HolidayEntity> l) {
		return l == null ? null : l.stream().map(h -> toModel(h)).collect(Collectors.toList());
	}

	@Override
	public Holiday findByPrimaryKey(Long id) {
		return null;
	}

	@Override
	public LocalDate nextWorkDay(LocalDate d) {
		do {
			d = d.plusDays(1L);
		} while (d.getDayOfWeek() == SUNDAY || isAHoliday(d));
		return d;
	}

	private boolean isAHoliday(LocalDate d) {
		return repository.findByDeclaredDate(d) != null;
	}

	@Override
	protected Holiday toModel(HolidayEntity e) {
		Holiday h = new Holiday();
		h.setDeclaredDate(e.getDeclaredDate());
		h.setName(e.getName());
		h.setCreatedBy(e.getCreatedBy());
		h.setCreatedOn(e.getCreatedOn());
		return h;
	}

	@Override
	protected HolidayEntity toEntity(Holiday t) {
		HolidayEntity e = new HolidayEntity();
		e.setDeclaredDate(t.getDeclaredDate());
		e.setName(t.getName());
		return e;
	}
}