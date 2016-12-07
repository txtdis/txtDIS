package ph.txtdis.service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.HolidayEntity;
import ph.txtdis.dto.Holiday;
import ph.txtdis.repository.HolidayRepository;

@Service("holidayService")
public class HolidayServiceImpl extends AbstractIdService<HolidayRepository, HolidayEntity, Holiday, Long>
		implements HolidayService {

	@Override
	public Holiday findByDate(Date d) {
		HolidayEntity e = repository.findByDeclaredDate(d.toLocalDate());
		return e == null ? null : toDTO(e);
	}

	@Override
	public List<Holiday> list() {
		List<HolidayEntity> l = repository.findByOrderByDeclaredDateDesc();
		return convert(l);
	}

	private List<Holiday> convert(List<HolidayEntity> l) {
		return l == null ? null : l.stream().map(h -> toDTO(h)).collect(Collectors.toList());
	}

	@Override
	public Holiday findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Holiday toDTO(HolidayEntity e) {
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