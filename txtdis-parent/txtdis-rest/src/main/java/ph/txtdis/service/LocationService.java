package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Location;

public interface LocationService {

	List<Location> listBarangays(String city);

	List<Location> listCities(String province);

	List<Location> listProvinces();
}