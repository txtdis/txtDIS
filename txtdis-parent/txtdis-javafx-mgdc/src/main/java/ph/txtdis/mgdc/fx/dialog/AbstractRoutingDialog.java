package ph.txtdis.mgdc.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.service.RouteAssignedCustomerService;
import ph.txtdis.mgdc.service.RouteService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractRoutingDialog<CS extends RouteAssignedCustomerService> //
	extends AbstractFieldDialog<Routing> //
	implements RoutingDialog {

	@Autowired
	protected CS customerService;

	@Autowired
	private RouteService routeService;

	@Autowired
	private LabeledCombo<Route> routeCombo;

	@Autowired
	private LabeledDatePicker startDatePicker;

	private Routing routing;

	@Override
	protected List<InputNode<?>> addNodes() {
		routeCombo.name("Route").items(getRoutes()).build();
		return asList(routeCombo, startDatePicker());
	}

	protected List<Route> getRoutes() {
		try {
			return routeService.list();
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.onAction(value -> createRouteAssignmentUponValidation());
		return startDatePicker;
	}

	private void createRouteAssignmentUponValidation() {
		try {
			if (startDatePicker.getValue() != null)
				routing =
					customerService.createRouteAssignmentUponValidation(routeCombo.getValue(), startDatePicker.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected Routing createEntity() {
		return routing;
	}

	@Override
	protected String headerText() {
		return "Add New Routing";
	}
}
