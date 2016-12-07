package ph.txtdis.service;

import ph.txtdis.domain.LocationEntity;

public interface ImportedLocationService extends LocationService, Imported {

	LocationEntity getByName(String name);
}
