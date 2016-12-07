package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Holiday;
import ph.txtdis.info.Information;

public interface HolidayService extends Listed<Holiday>, Titled {

	boolean isAHoliday(LocalDate d);

	boolean isOffSite();

	LocalDate nextWorkDay(LocalDate today);

	Holiday save(LocalDate date, String name) throws Information, Exception;

	void validateDate(LocalDate d) throws Exception;
}