package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Holiday;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.SyncService;
import ph.txtdis.util.ClientTypeMap;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.username;

@Service("holidayService")
public class HolidayServiceImpl
	implements HolidayService {

	@Autowired
	private RestClientService<Holiday> restClientService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public RestClientService<Holiday> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + HolidayService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public LocalDate previousWorkDay(LocalDate date) {
		do {
			date = todayIfNull(date);
			date = date.minusDays(1L);
		} while (date.getDayOfWeek() == DayOfWeek.SUNDAY || isAHoliday(date));
		return date;
	}

	private LocalDate todayIfNull(LocalDate date) {
		if (date == null)
			date = getServerDate();
		return date;
	}

	@Override
	public boolean isAHoliday(LocalDate d) {
		if (d != null)
			try {
				return holiday(d) != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}

	private Holiday holiday(LocalDate d) throws Exception {
		return holidayService().getOne("/find?date=" + d);
	}

	private RestClientService<Holiday> holidayService() {
		return restClientService.module(getModuleName());
	}

	@Override
	public String getModuleName() {
		return "holiday";
	}

	@Override
	public LocalDate nextWorkDay(LocalDate date) {
		do {
			date = todayIfNull(date);
			date = date.plusDays(1L);
		} while (date.getDayOfWeek() == DayOfWeek.SUNDAY || isAHoliday(date));
		return date;
	}

	@Override
	public void reset() {
	}

	@Override
	public Holiday save(LocalDate date, String name) throws Information, Exception {
		Holiday h = new Holiday();
		h.setDeclaredDate(date);
		h.setName(name);
		return holidayService().save(h);
	}

	@Override
	public void validateDate(LocalDate d) throws Exception {
		if (d == null)
			return;
		if (holiday(d) != null)
			throw new DuplicateException(toDateDisplay(d));
	}
}
