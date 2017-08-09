package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.gsm.service.ItineraryRouteService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractGsmRouteItineraryDialog //
	extends AbstractInputDialog {

	@Autowired
	protected ItineraryRouteService routeService;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppCombo<Route> routeCombo;

	private Route route;

	@Override
	protected List<AppButton> buttons() {
		return asList(openButton(), closeButton());
	}

	private AppButton openButton() {
		AppButton openButton = button.large("Generate").build();
		openButton.onAction(event -> setSelectedRoute());
		openButton.disableIf(routeCombo.isEmpty());
		return openButton;
	}

	private void setSelectedRoute() {
		route = routeCombo.getValue();
		routeCombo.setValue(null);
		close();
	}

	public Route getRoute() {
		return route;
	}

	@Override
	public void goToDefaultFocus() {
		routeCombo.requestFocus();
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Route"), 0, 0);
		grid.add(routeCombo.width(180).items(getRoutes()), 1, 0);
		return asList(header(), grid, buttonBox());
	}

	protected abstract List<Route> getRoutes();

	@Override
	protected Label header() {
		return label.dialog("Select Route");
	}

	@Override
	protected void nullData() {
		super.nullData();
		route = null;
	}
}
