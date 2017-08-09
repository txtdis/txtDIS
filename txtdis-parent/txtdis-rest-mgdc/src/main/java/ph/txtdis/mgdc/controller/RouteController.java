package ph.txtdis.mgdc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.service.server.RouteService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController("routeController")
@RequestMapping("/routes")
public class RouteController
	extends AbstractNameListController<RouteService, Route> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findById(@RequestParam("id") Long id) {
		Route t = service.findByPrimaryKey(id);
		return new ResponseEntity<>(t, HttpStatus.OK);
	}

	@RequestMapping(path = "/extruck", method = GET)
	public ResponseEntity<?> listExTruckRoutes() {
		List<Route> l = service.listExTruckRoutes();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/presell", method = GET)
	public ResponseEntity<?> listPreSellRoutes() {
		List<Route> l = service.listPreSellRoutes();
		return new ResponseEntity<>(l, OK);
	}
}