package ph.txtdis.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.DayOfWeek;
import java.time.LocalDate;

public interface NextWorkDayDated {

	HolidayService getHolidayService();

	default boolean isTheNextWorkDay(LocalDate date) {
		long day = 1L;
		while (isPreviousDayANonWorkingDay(date, day))
			++day;
		return LocalDate.now().until(date, DAYS) == day;
	}

	default boolean isPreviousDayANonWorkingDay(LocalDate date, long day) {
		LocalDate previous = date.minusDays(day);
		if (previous.equals(LocalDate.now()))
			return false;
		return previous.getDayOfWeek() == DayOfWeek.SUNDAY || getHolidayService().isAHoliday(previous);
	}
}
