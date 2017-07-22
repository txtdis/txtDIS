package ph.txtdis.mgdc.service;

import java.time.LocalDate;

import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.service.ResettableService;

public interface LoadingService //
		extends NextWorkDayDated, ResettableService,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	LocalDate getPickDate();
}
