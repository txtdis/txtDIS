package ph.txtdis.mgdc.service;

import java.time.LocalDate;

import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;

public interface RouteAssignedCustomerService {

	Routing createRouteAssignmentUponValidation(Route r, LocalDate start) throws Exception;
}
