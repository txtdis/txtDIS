package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsBusinessAddress;
import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.repository.EdmsBusinessAddressRepository;
import ph.txtdis.type.LocationType;

@Service("locationService")
public class EdmsLocationServiceImpl //
		implements EdmsLocationService {

	@Value("${client.province}")
	private String province;

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
	private EdmsBusinessAddressRepository repository;

	@Autowired
	private EdmsCustomerService customerService;

	@Override
	public String getBarangay(Customer c) {
		return edmsBarangay(edmsProvince(c), edmsCity(c), edmsBarangay(c));
	}

	private String edmsProvince(Customer c) {
		try {
			return c.getProvince().getName();
		} catch (Exception e) {
			return "";
		}
	}

	private String edmsCity(Customer c) {
		try {
			return c.getCity().getName();
		} catch (Exception e) {
			return "";
		}
	}

	private String edmsBarangay(Customer c) {
		try {
			return c.getBarangay().getName();
		} catch (Exception e) {
			return "";
		}
	}

	private String edmsBarangay(String province, String city, String barangay) {
		EdmsBusinessAddress a = repository.findFirstByProvinceAndCityAndBarangayContainingAllIgnoreCase(province, city, barangay);
		return a == null ? "" : a.getBarangay();
	}

	@Override
	public String getCity(Customer c) {
		return edmsCity(edmsProvince(c), edmsCity(c));
	}

	private String edmsCity(String province, String city) {
		List<EdmsBusinessAddress> l = repository.findByProvinceAndCityContainingAllIgnoreCase(province, city);
		return l == null || l.isEmpty() ? "" : l.get(0).getCity();
	}

	@Override
	public Location getBarangay(String barangay, String city) {
		return toBarangay(barangay);
	}

	@Override
	public String getProvince(Customer c) {
		return edmsProvince(edmsProvince(c));
	}

	private String edmsProvince(String name) {
		EdmsBusinessAddress a = repository.findFirstByProvinceContainingIgnoreCase(province);
		return a == null ? "" : a.getProvince();
	}

	@Override
	public List<Location> listBarangays(String city) {
		List<String> brgys = repository.findByProvinceAndCityContainingAllIgnoreCase(province, city) //
				.stream().map(a -> a.getBarangay()) //
				.collect(toList());
		return brgys.stream().map(b -> toBarangay(b)).collect(Collectors.toList());
	}

	@Override
	public Location toBarangay(String b) {
		return toLocation(b, LocationType.BARANGAY);
	}

	private Location toLocation(String name, LocationType t) {
		return name == null ? null : newLocation(name, t);
	}

	private Location newLocation(String name, LocationType t) {
		Location l = new Location();
		l.setName(name.toUpperCase().trim());
		l.setType(t);
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