package ph.txtdis.controller;

import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Location;
import ph.txtdis.domain.LocationTree;
import ph.txtdis.repository.LocationRepository;
import ph.txtdis.repository.LocationTreeRepository;
import ph.txtdis.type.LocationType;

@RestController("locationController")
@RequestMapping("/locations")
public class LocationController {

	@Autowired
	private LocationRepository repository;

	@Autowired
	private LocationTreeRepository treeRepository;

	@RequestMapping(path = "/barangays", method = RequestMethod.GET)
	public ResponseEntity<?> listBarangays(@RequestParam("of") Location city) {
		List<Location> list = getLocations(BARANGAY, city);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/cities", method = RequestMethod.GET)
	public ResponseEntity<?> listCities(@RequestParam("of") Location province) {
		List<Location> list = getLocations(CITY, province);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/provinces", method = RequestMethod.GET)
	public ResponseEntity<?> listProvinces() {
		Iterable<Location> list = repository.findByTypeOrderByNameAsc(PROVINCE);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	private List<Location> getLocations(LocationType type, Location city) {
		List<LocationTree> trees = treeRepository.findByLocationTypeAndParentOrderByLocationNameAsc(type, city);
		List<Location> list = trees.stream().map(t -> t.getLocation()).collect(Collectors.toList());
		return list;
	}
}