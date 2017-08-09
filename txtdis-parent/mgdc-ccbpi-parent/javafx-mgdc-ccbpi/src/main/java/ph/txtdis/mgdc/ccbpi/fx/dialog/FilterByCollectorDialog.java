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
import ph.txtdis.service.UserService;
import ph.txtdis.type.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Scope("prototype")
@Component("filterByCollectorDialog")
public class FilterByCollectorDialog
	extends AbstractInputDialog {

	private static final String ALL = "ALL";

	@Autowired
	private AppCombo<String> collectorCombo;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private UserService userService;

	@Override
	protected AppButton closeButton() {
		return closeButton("Generate");
	}

	public String getCollector() {
		return collectorCombo.getValue();
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Name"), 0, 0);
		grid.add(collectorCombo(), 1, 0);
		return Arrays.asList(header(), grid, buttonBox());
	}

	private AppCombo<String> collectorCombo() {
		collectorCombo.items(collectors());
		collectorCombo.select(ALL);
		return collectorCombo;
	}

	@Override
	protected Label header() {
		return label.dialog("Filter by Collector");
	}

	private List<String> collectors() {
		List<String> collectors = new ArrayList<>(Arrays.asList(ALL));
		collectors.addAll(userService.listNamesByRole(UserType.COLLECTOR));
		return collectors;
	}

	@Override
	public void goToDefaultFocus() {
		collectorCombo.requestFocus();
	}
}
