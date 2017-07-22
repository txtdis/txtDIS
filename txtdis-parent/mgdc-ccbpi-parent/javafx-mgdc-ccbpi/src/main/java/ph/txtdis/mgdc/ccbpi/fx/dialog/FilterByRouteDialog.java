package ph.txtdis.mgdc.ccbpi.fx.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.ccbpi.service.ChannelService;

@Scope("prototype")
@Component("filterByRouteDialog")
public class FilterByRouteDialog //
		extends AbstractInputDialog {

	private static final String ALL = "ALL";

	@Autowired
	private AppCombo<String> routeCombo;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private LabelFactory label;

	@Autowired
	private ChannelService channelService;

	@Override
	protected Button closeButton() {
		return closeButton("Generate");
	}

	public String getRoute() {
		return routeCombo.getValue();
	}

	@Override
	protected Label header() {
		return label.dialog("Filter by Route");
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Route"), 0, 0);
		grid.add(routeCombo(), 1, 0);
		return Arrays.asList(header(), grid, buttonBox());
	}

	private AppCombo<String> routeCombo() {
		routeCombo.items(routes());
		routeCombo.select(ALL);
		return routeCombo;
	}

	private List<String> routes() {
		List<String> routes = new ArrayList<>(Arrays.asList(ALL));
		routes.addAll(channelService.listNames());
		return routes;
	}

	@Override
	public void goToDefaultFocus() {
		routeCombo.requestFocus();
	}
}
