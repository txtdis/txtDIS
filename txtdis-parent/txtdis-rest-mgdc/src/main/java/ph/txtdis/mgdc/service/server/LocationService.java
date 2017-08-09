package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.domain.LocationEntity;

import java.util.List;

public interface LocationService {

	List<Location> listBarangays(String city);

	List<Location> listCities(String province);

	List<Location> listProvinces();

	LocationEntity toEntity(Location l);

	Location toModel(LocationEntity e);
}