package ph.txtdis.service;

import ph.txtdis.domain.LocationEntity;
import ph.txtdis.dto.Location;

public interface PrimaryLocationService extends LocationService {

	Location toDTO(LocationEntity e);

	LocationEntity toEntity(Location l);
}
