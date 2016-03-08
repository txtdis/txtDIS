package ph.txtdis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Route;
import ph.txtdis.repository.RouteRepository;

@RestController("routeController")
@RequestMapping("/routes")
public class RouteController extends NameListController<RouteRepository, Route> {

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> findById(@RequestParam("id") Long id) {
		Route entity = repository.findOne(id);
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
}