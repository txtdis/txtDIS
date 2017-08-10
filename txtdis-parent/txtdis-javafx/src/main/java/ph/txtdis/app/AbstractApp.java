package ph.txtdis.app;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.pane.PaneFactory;
import ph.txtdis.info.Information;
import ph.txtdis.service.ResettableService;
import ph.txtdis.util.FontIcon;
import ph.txtdis.util.TypeMap;

import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Pos.TOP_RIGHT;
import static javafx.scene.layout.HBox.setHgrow;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.stage.Modality.WINDOW_MODAL;

public abstract class AbstractApp<RS extends ResettableService>
	extends Stage
	implements App {

	@Autowired
	protected LabelFactory label;

	@Autowired
	protected PaneFactory pane;

	@Autowired
	protected TypeMap typeMap;

	@Autowired
	protected RS service;

	private Label header;

	private TilePane buttons;

	@Override
	public AbstractApp<RS> addParent(Stage stage) {
		if (getOwner() == null)
			initialize(stage);
		return this;
	}

	private void initialize(Stage stage) {
		initOwner(stage);
		initModality(WINDOW_MODAL);
	}

	@Override
	public void initialize() {
		service.reset();
		setStage(mainVerticalPane());
		setListeners();
		setBindings();
		refresh();
	}

	@Override
	public void start() {
		initialize();
		show();
	}

	protected void setStage(VBox pane) {
		getIcons().add(icon());
		refreshTitleAndHeader();
		setScene(scene(pane));
		setBounds();
	}

	private Image icon() {
		return new FontIcon(getFontIcon());
	}

	protected abstract String getFontIcon();

	private Scene scene(VBox pane) {
		Scene scene = new Scene(pane);
		scene.getStylesheets().addAll("/css/base.css");
		return scene;
	}

	private void setBounds() {
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		setMaxHeight(bounds.getHeight());
		setMaxWidth(bounds.getWidth());
	}

	protected final VBox mainVerticalPane() {
		VBox vbox = pane.vertical(headerPane());
		vbox.getChildren().add(mainVerticalCenteredPane());
		return vbox;
	}

	private HBox headerPane() {
		setButtonPane();
		HBox hBox = pane.horizontal(header(), buttons);
		setHgrow(buttons, ALWAYS);
		hBox.setPadding(new Insets(10, 10, 0, 10));
		return hBox;
	}

	private void setButtonPane() {
		buttons = new TilePane();
		buttons.getChildren().addAll(addButtons());
		buttons.setHgap(5);
		buttons.setAlignment(TOP_RIGHT);
	}

	protected List<AppButton> addButtons() {
		return new ArrayList<>();
	}

	private Label header() {
		header = label.header(getHeaderText());
		header.setPadding(new Insets(0, 30, 0, 0));
		return header;
	}

	private VBox mainVerticalCenteredPane() {
		return pane.topCenteredVertical(mainVerticalPaneNodes());
	}

	protected abstract List<Node> mainVerticalPaneNodes();

	protected void setListeners() {
		setOnCloseRequest(e -> clear());
		setOnHiding(e -> clear());
	}

	protected void clear() {
		service.reset();
	}

	protected void setBindings() {
	}

	protected void renew() {
		service.reset();
		refresh();
	}

	@Override
	public void refresh() {
		refreshTitleAndHeader();
		goToDefaultFocus();
	}

	protected void refreshTitleAndHeader() {
		setTitle(getTitleText());
		if (header != null)
			header.setText(getHeaderText());
	}

	protected abstract String getTitleText();

	protected abstract String getHeaderText();

	protected void showErrorDialog(Exception e) {
		e.printStackTrace();
		showErrorDialog(e);
	}

	protected void showInfoDialog(Information i) {
		messageDialog().show(i).addParent(this).start();
	}

	@Lookup
	public MessageDialog messageDialog() {
		return null;
	}
}
