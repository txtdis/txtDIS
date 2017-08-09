package ph.txtdis.fx.tab;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.control.ErrorHandling;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.pane.PaneFactory;
import ph.txtdis.fx.pane.AppGridPane;

public abstract class AbstractTab
	extends Tab
	implements InputTab {

	@Autowired
	protected PaneFactory pane;

	@Autowired
	protected MessageDialog dialog;

	@Autowired
	protected AppGridPane gridPane;

	public AbstractTab(String name) {
		super();
		setText(name);
	}

	public AbstractTab() {
		setContent(null);
	}

	@Override
	public AbstractTab asTab() {
		return this;
	}

	@Override
	public InputTab build() {
		setContent(mainVerticalPane());
		setListeners();
		setBindings();
		return this;
	}

	protected VBox mainVerticalPane() {
		return pane.vertical(mainVerticalPaneNodes());
	}

	protected abstract void setListeners();

	protected abstract void setBindings();

	protected abstract List<Node> mainVerticalPaneNodes();

	@Override
	public void disableIf(ObservableBooleanValue b) {
		disableProperty().bind(b);
	}

	@Override
	public void select() {
		getTabPane().getSelectionModel().select(this);
	}

	protected void handleError(ErrorHandling control, Exception e) {
		dialog.show(e).addParent(this).start();
		control.handleError();
	}
}
