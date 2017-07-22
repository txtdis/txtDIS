package ph.txtdis.mgdc.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.DayOfWeek;
import java.time.LocalDate;

import ph.txtdis.service.SyncService;

public interface NextWorkDayDated {

	HolidayService getHolidayService();

	SyncService getSyncService();

	default boolean isTheNextWorkDay(LocalDate date) {
		long day = 1L;
		while (isPreviousDayANonWorkingDay(date, day))
			++day;
		return getSyncService().getServerDate().until(date, DAYS) == day;
	}

	default boolean isPreviousDayANonWorkingDay(LocalDate date, long day) {
		LocalDate previous = date.minusDays(day);
		if (previous.equals(getSyncService().getServerDate()))
			return false;
		return previous.getDayOfWeek() == DayOfWeek.SUNDAY || getHolidayService().isAHoliday(previous);
	}
}
