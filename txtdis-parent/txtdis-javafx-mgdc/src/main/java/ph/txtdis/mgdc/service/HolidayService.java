package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Holiday;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

import java.time.LocalDate;

public interface HolidayService
	extends ListedAndResettableService<Holiday>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	boolean isAHoliday(LocalDate d);

	LocalDate nextWorkDay(LocalDate today);

	LocalDate previousWorkDay(LocalDate today);

	Holiday save(LocalDate date, String name) throws Information, Exception;

	void validateDate(LocalDate d) throws Exception;
}