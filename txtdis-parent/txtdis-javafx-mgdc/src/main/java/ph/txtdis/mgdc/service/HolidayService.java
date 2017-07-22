package ph.txtdis.mgdc.service;

import java.time.LocalDate;

import ph.txtdis.dto.Holiday;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface HolidayService extends ListedAndResetableService<Holiday>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	boolean isAHoliday(LocalDate d);

	LocalDate nextWorkDay(LocalDate today);

	LocalDate previousWorkDay(LocalDate today);

	Holiday save(LocalDate date, String name) throws Information, Exception;

	void validateDate(LocalDate d) throws Exception;
}