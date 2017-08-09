package ph.txtdis.mgdc.gsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.service.server.LocationService;

import java.util.List;

@RequestMapping("/locations")
@RestController("locationController")
public class LocationController {

	@Autowired
	private LocationService service;

	@RequestMapping(path = "/barangays", method = RequestMethod.GET)
	public ResponseEntity<?> barangays(@RequestParam("of") String city) {
		List<Location> list = service.listBarangays(city);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/cities", method = RequestMethod.GET)
	public ResponseEntity<?> cities(@RequestParam("of") String province) {
		List<Location> list = service.listCities(province);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/provinces", method = RequestMethod.GET)
	public ResponseEntity<?> provinces() {
		List<Location> list = service.listProvinces();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}