package ph.txtdis.service;

import java.time.ZonedDateTime;

import ph.txtdis.dto.Keyed;

public interface MasterService<T extends Keyed<Long>> //
		extends DeactivationService<Long>, DecisionNeededService, SearchedByNameService<T>,
		SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long>, UniqueNamedService<T> {

	String getLastModifiedBy();

	ZonedDateTime getLastModifiedOn();
}
