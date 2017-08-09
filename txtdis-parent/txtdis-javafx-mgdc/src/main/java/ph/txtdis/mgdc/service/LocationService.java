package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Location;

import java.util.List;

public interface LocationService {

	List<Location> listBarangays(Location city);

	List<Location> listCities(Location province);

	List<Location> listProvinces();

}