package ph.txtdis.mgdc.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.RouteService;

@Scope("prototype")
@Component("routeDialog")
public class RouteDialog //
	extends AbstractNameListDialog<Route, RouteService> {

	@Override
	protected Route createEntity() {
		try {
			return service.save(nameField.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Route";
	}
}
