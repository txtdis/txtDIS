package ph.txtdis.service;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.dto.Truck;

public interface ServerTruckService //
		extends SavedNameListService<Truck> {

	TruckEntity findEntityByName(String truck);

	TruckEntity toEntity(Truck t);

	Truck toModel(TruckEntity t);
}
