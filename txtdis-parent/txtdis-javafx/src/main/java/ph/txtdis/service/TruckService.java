package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Truck;

public interface TruckService extends ListedAndResetableService<Truck>, SavedByName<Truck>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, UniqueNamedService<Truck> {

	List<String> listNames();
}