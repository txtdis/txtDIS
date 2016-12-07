package ph.txtdis.service;

import ph.txtdis.domain.TruckEntity;

public interface PrimaryTruckService extends TruckService {

	TruckEntity findEntityByName(String truck);
}
