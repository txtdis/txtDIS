package ph.txtdis.mgdc.service;

import ph.txtdis.service
	.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.service.ResettableService;

import java.time.LocalDate;

public interface LoadingService //
	extends NextWorkDayDated,
	ResettableService,
	RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	LocalDate getPickDate();
}
