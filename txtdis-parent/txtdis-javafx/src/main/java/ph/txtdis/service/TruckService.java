package ph.txtdis.service;

import ph.txtdis.dto.Truck;

import java.util.List;

public interface TruckService
	extends ListedAndResettableService<Truck>,
	SavedByName<Truck>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	UniqueNamedService<Truck> {

	List<String> listNames();
}