package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.Holiday;
import ph.txtdis.service.SavedService;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService //
	extends SavedService<Holiday> {

	Holiday findByDate(LocalDate d);

	List<Holiday> list();

	LocalDate nextWorkDay(LocalDate d);
}