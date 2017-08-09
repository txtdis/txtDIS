package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.RadioControl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.RestServerService;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

@Scope("prototype")
@Component("serverSelectionDialog")
public class ServerSelectionDialog
	extends AbstractInputDialog {

	@Autowired
	private RestServerService serverService;

	@Autowired
	private AppGridPane grid;

	private ToggleGroup group;

	@Override
	public void goToDefaultFocus() {
		closeButton.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return singletonList(closeButton("OK"));
	}

	@Override
	protected String headerText() {
		return "Choose Server";
	}

	@Override
	protected List<Node> nodes() {
		group = new ToggleGroup();
		putButtonOnTheGrid(serverService.getLocations());
		selectTheDefaultServer();
		return Arrays.asList(header(), grid, buttonBox());
	}

	private void putButtonOnTheGrid(List<String> list) {
		grid.getChildren().clear();
		for (int i = 0; i < list.size(); i++)
			grid.add(radioButton(list.get(i)), 0, i);
	}

	private void selectTheDefaultServer() {
		group.getToggles().stream().filter(t -> ((RadioButton) t).getText().equals(serverService.getLocation()))
			.findAny()
			.get()
			.setSelected(true);
	}

	private RadioControl radioButton(String s) {
		RadioControl radio = new RadioControl(s);
		radio.setToggleGroup(group);
		return radio;
	}

	@Override
	protected void setOnClickedCloseButton() {
		RadioButton rb = (RadioButton) group.getSelectedToggle();
		serverService.setLocation(rb.getText());
		super.setOnClickedCloseButton();
	}
}
