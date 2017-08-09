package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Location;
import ph.txtdis.service.RestClientService;

import java.util.List;

import static java.util.Collections.emptyList;

@Service("locationService")
public class LocationServiceImpl
	implements LocationService {

	private static final String LOCATION = "location";

	@Autowired
	private RestClientService<Location> restClientService;

	@Override
	public List<Location> listBarangays(Location city) {
		try {
			return getList("/barangays?of=" + city.getName());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private List<Location> getList(String endPt) throws Exception {
		return restClientService.module(LOCATION).getList(endPt);
	}

	@Override
	public List<Location> listCities(Location province) {
		try {
			return getList("/cities?of=" + province.getName());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<Location> listProvinces() {
		try {
			return getList("/provinces");
		} catch (Exception e) {
			return emptyList();
		}
	}
}
