package ph.txtdis.mgdc.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;
import static ph.txtdis.util.DateTimeUtils.getServerDate;

public interface NextWorkDayDated {

	default boolean isTheNextWorkDay(LocalDate date) {
		long day = 1L;
		while (isPreviousDayANonWorkingDay(date, day))
			++day;
		return getServerDate().until(date, DAYS) == day;
	}

	default boolean isPreviousDayANonWorkingDay(LocalDate date, long day) {
		LocalDate previous = date.minusDays(day);
		if (previous.equals(getServerDate()))
			return false;
		return previous.getDayOfWeek() == DayOfWeek.SUNDAY || getHolidayService().isAHoliday(previous);
	}

	HolidayService getHolidayService();
}
