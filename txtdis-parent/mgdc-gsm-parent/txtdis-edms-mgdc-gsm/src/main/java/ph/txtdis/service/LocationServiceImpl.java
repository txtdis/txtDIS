package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsBusinessAddress;
import ph.txtdis.dto.Location;
import ph.txtdis.repository.EdmsBarangayRepository;
import ph.txtdis.type.LocationType;

@Service("locationService")
public class LocationServiceImpl implements EdmsLocationService {

	private static Logger logger = getLogger(LocationServiceImpl.class);

	private static final String NCR = "NCR";

	public static final String MANILA = "MANILA";

	public static final List<String> MANILA_DISTRICTS = Arrays.asList(//
			"BINONDO", //
			"ERMITA", //
			"INTRAMUROS", //
			"MALATE", //
			"PACO", //
			"PANDACAN", //
			"PORT AREA", //
			"QUIAPO", //
			"SAMPALOC", //
			"SAN ANDRES", //
			"SAN MIGUEL", //
			"SAN NICOLAS", //
			"SANTA ANA", //
			"SANTA CRUZ", //
			"SANTA MESA", //
			"TONDO");

	@Autowired
	private EdmsBarangayRepository barangayRepository;

	@Autowired
	private CustomerService customerService;

	@Override
	public Location getBarangay(String barangay, String city) {
		EdmsBusinessAddress b = barangayRepository.findByCityAndBarangayAllIgnoreCase(city, barangay);
		return b == null ? null : toBarangay(b.getBarangay());
	}

	@Override
	public List<Location> listBarangays(String city) {
		List<String> brgys = city.equals(MANILA) ? MANILA_DISTRICTS
				: barangayRepository.findByProvinceAndCityContainingAllIgnoreCase(NCR, city).stream()
						.map(a -> a.getBarangay()).collect(Collectors.toList());
		return brgys.stream().map(b -> toBarangay(b)).collect(Collectors.toList());
	}

	@Override
	public Location toBarangay(String b) {
		return toLocation(b, LocationType.BARANGAY);
	}

	private Location toLocation(String name, LocationType t) {
		Location l = new Location();
		l.setName(name.toUpperCase().trim());
		l.setType(t);
		logger.info("\n    Location: " + l.getType() + " - " + l.getName());
		return l;
	}

	@Override
	public List<Location> listCities(String province) {
		List<String> cities = customerService.getCities(province);
		return cities.stream().map(p -> toCity(p)).collect(Collectors.toList());
	}

	@Override
	public Location toCity(String city) {
		return toLocation(city, LocationType.CITY);
	}

	@Override
	public List<Location> listProvinces() {
		List<String> provinces = customerService.getProvinces();
		return provinces.stream().map(p -> toProvince(p)).collect(Collectors.toList());
	}

	@Override
	public Location toProvince(String province) {
		return toLocation(province, LocationType.PROVINCE);
	}
}