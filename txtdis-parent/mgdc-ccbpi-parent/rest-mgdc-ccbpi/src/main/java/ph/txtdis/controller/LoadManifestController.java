package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.LoadManifestService;

@RequestMapping("/loadManifests")
@RestController("loadManifestController")
public class LoadManifestController extends AbstractBillableController<LoadManifestService> {

	@RequestMapping(path = "/loadManifest", method = GET)
	public ResponseEntity<?> findLoadManifest(@RequestParam("shipment") Long id) {
		Billable i = service.find(id);
		return new ResponseEntity<>(i, OK);
	}
}