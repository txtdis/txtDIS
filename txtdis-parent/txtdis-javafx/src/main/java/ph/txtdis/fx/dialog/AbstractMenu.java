package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ph.txtdis.app.App;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.ButtonFactory;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.util.StyleSheet;

public abstract class AbstractMenu //
	extends Stage {

	@Autowired
	protected ButtonFactory button;

	@Autowired
	protected LabelFactory label;

	@Autowired
	protected StyleSheet styleSheet;

	@Value("${prefix.module}")
	protected String modulePrefix;

	protected Scene createScene() {
		Scene s = new Scene(dialogBox());
		s.getStylesheets().add("/css/base.css");
		return s;
	}

	private Parent dialogBox() {
		HBox b = new HBox(gridPane());
		b.setPadding(new Insets(10));
		b.setAlignment(Pos.CENTER);
		return b;
	}

	private GridPane gridPane() {
		GridPane gp = new GridPane();
		gp.setHgap(5);
		gp.setVgap(5);
		gp.setAlignment(Pos.CENTER);
		return addGridPaneNodes(gp);
	}

	protected abstract GridPane addGridPaneNodes(GridPane gp);

	protected AppButton button(App app, AppButton button, String icon) {
		button.fontSize(44).icon(icon).build();
		button.onAction(e -> app.start());
		return button;
	}

	protected AppButton button(String icon) {
		return button.fontSize(44).icon(icon).onAction(e -> startApp(icon)).build();
	}

	private void startApp(String icon) {
		App app = getApp(icon);
		if (app != null)
			app.start();
	}

	protected App getApp(String icon) {
		return null;
	}
}
