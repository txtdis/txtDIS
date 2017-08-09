package ph.txtdis.mgdc.ccbpi.fx.dialog;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.ccbpi.service.ChannelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

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
	private ChannelService channelService;

	@Override
	protected AppButton closeButton() {
		return closeButton("Generate");
	}

	public String getRoute() {
		return routeCombo.getValue();
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

	@Override
	protected Label header() {
		return label.dialog("Filter by Route");
	}

	private List<String> routes() {
		ArrayList<String> routes = new ArrayList<>(singletonList(ALL));
		routes.addAll(channelService.listNames());
		return routes;
	}

	@Override
	public void goToDefaultFocus() {
		routeCombo.requestFocus();
	}
}
