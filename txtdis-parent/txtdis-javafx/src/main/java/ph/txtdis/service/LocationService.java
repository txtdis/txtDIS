package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Location;

public interface LocationService {

	List<Location> listBarangays(Location city);

	List<Location> listCities(Location province);

	List<Location> listProvinces();

}