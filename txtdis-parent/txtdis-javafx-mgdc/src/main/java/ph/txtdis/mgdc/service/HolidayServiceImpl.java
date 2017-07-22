package ph.txtdis.mgdc.service;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Holiday;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.info.Information;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SyncService;
import ph.txtdis.util.ClientTypeMap;

@Service("holidayService")
public class HolidayServiceImpl implements HolidayService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Holiday> readOnlyService;

	@Autowired
	private SavingService<Holiday> savingService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public String getModuleName() {
		return "holiday";
	}

	@Override
	public ReadOnlyService<Holiday> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + HolidayService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
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
		return readOnlyService.module(getModuleName()).getOne("/find?date=" + d);
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
			date = syncService.getServerDate();
		return date;
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
		return savingService.module(getModuleName()).save(h);
	}

	@Override
	public void validateDate(LocalDate d) throws Exception {
		if (d == null)
			return;
		if (holiday(d) != null)
			throw new DuplicateException(toDateDisplay(d));
	}
}
