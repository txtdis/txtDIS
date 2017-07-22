package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.service.server.LoadManifestService;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;

@RequestMapping("/loadManifests")
@RestController("loadManifestController")
public class LoadManifestController //
		extends AbstractSpunSavedReferencedKeyedController<LoadManifestService, BillableEntity, Billable> {

	@RequestMapping(path = "/lm", method = GET)
	public ResponseEntity<?> find(@RequestParam("shipment") Long id) {
		Billable i = service.find(id);
		return new ResponseEntity<>(i, OK);
	}
}