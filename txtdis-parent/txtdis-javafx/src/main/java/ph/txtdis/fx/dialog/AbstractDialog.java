package ph.txtdis.fx.dialog;

import static javafx.geometry.Pos.CENTER;
import static javafx.stage.Modality.WINDOW_MODAL;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ph.txtdis.app.StartableApp;

public abstract class AbstractDialog //
		extends Stage //
		implements StartableApp {

	private String style;

	public AbstractDialog() {
		style = "-fx-border-color: derive(-fx-base, -20%); " //
				+ "-fx-border-radius: 0.5em; -fx-background-radius: 0.5em; ";
	}

	public StartableApp addParent(Node node) {
		Scene scene = node.getScene();
		Stage stage = (Stage) scene.getWindow();
		return addParent(stage);
	}

	public StartableApp addParent(Pane pane) {
		Parent parent = pane.getParent();
		return addParent(parent);
	}

	@Override
	public StartableApp addParent(Stage stage) {
		if (stage != null && stage.getOwner() == null && getOwner() == null)
			initialize(stage);
		return this;
	}

	public StartableApp addParent(Tab tab) {
		TabPane pane = tab.getTabPane();
		return addParent(pane);
	}

	@Override
	public void start() {
		initialize();
		showAndWait();
	}

	@Override
	public void initialize() {
		setScene(scene());
		refresh();
	}

	public StartableApp updateStyle(String style) {
		this.style += style;
		return this;
	}

	private void initialize(Stage stage) {
		initOwner(stage);
		initModality(WINDOW_MODAL);
		initStyle(StageStyle.TRANSPARENT);
	}

	private Scene scene() {
		Scene s = new Scene(vbox());
		s.getStylesheets().addAll("/css/base.css");
		s.setFill(Color.TRANSPARENT);
		return s;
	}

	private VBox vbox() {
		VBox vb = new VBox();
		vb.getChildren().setAll(nodes());
		vb.setAlignment(CENTER);
		vb.setPadding(new Insets(0, 20, 0, 20));
		vb.setStyle(style);
		return vb;
	}

	protected abstract List<Node> nodes();
}
