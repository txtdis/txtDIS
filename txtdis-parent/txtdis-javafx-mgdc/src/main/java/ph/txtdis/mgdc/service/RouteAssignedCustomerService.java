package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;

import java.time.LocalDate;

public interface RouteAssignedCustomerService {

	Routing createRouteAssignmentUponValidation(Route r, LocalDate start) throws Exception;
}
