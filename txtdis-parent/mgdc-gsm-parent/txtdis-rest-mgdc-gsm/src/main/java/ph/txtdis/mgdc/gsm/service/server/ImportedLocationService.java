package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.service.server.LocationService;

public interface ImportedLocationService //
	extends LocationService,
	Imported {

	LocationEntity getByName(String name);
}
