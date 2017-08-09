package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Route;
import ph.txtdis.service.EdmsRouteService;

@RequestMapping("/routes")
@RestController("routeController")
public class RouteController //
	extends AbstractNameListController<EdmsRouteService, Route> {
}