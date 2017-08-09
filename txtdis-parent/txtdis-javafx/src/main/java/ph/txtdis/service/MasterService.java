package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

import java.time.ZonedDateTime;

public interface MasterService<T extends Keyed<Long>> //
	extends DeactivationService<Long>,
	DecisionNeededService,
	SearchedByNameService<T>,
	SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long>,
	UniqueNamedService<T> {

	String getLastModifiedBy();

	ZonedDateTime getLastModifiedOn();
}
