package ph.txtdis.fx.dialog;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.ItineraryRouteService;

public abstract class AbstractRouteItineraryDialog extends AbstractInputDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppButton openButton;

	@Autowired
	private LabelFactory label;

	@Autowired
	protected ItineraryRouteService routeService;

	@Autowired
	private AppCombo<Route> routeCombo;

	private Route route;

	public Route getRoute() {
		return route;
	}

	@Override
	public void setFocus() {
		routeCombo.requestFocus();
	}

	private Button openButton() {
		openButton.large("Generate").build();
		openButton.setOnAction(event -> setSelectedRoute());
		openButton.disableIf(routeCombo.isEmpty());
		return openButton;
	}

	private void setSelectedRoute() {
		route = routeCombo.getValue();
		routeCombo.setValue(null);
		close();
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { openButton(), closeButton() };
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Route"), 0, 0);
		grid.add(routeCombo.items(getRoutes()), 1, 0);
		return Arrays.asList(header(), grid, buttonBox());
	}

	protected abstract List<Route> getRoutes();

	@Override
	protected Label header() {
		return label.dialog("Select Route");
	}

	@Override
	protected void setOnFiredCloseButton() {
		route = null;
		super.setOnFiredCloseButton();
	}
}
