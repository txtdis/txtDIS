package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.InputControl;
import ph.txtdis.fx.control.TextAreaDisplay;
import ph.txtdis.fx.dialog.OpenByDateDialog;
import ph.txtdis.fx.dialog.OpenByIdDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.service.Iconed;
import ph.txtdis.service.Reset;
import ph.txtdis.service.Serviced;

public abstract class AbstractIdApp<AS extends Serviced<PK>, T extends Keyed<PK>, PK, ID> extends AbstractApp
		implements Launchable {

	@Autowired
	protected AS service;

	@Autowired
	protected AppButton decisionButton, newButton, backButton, openByIdButton, nextButton, saveButton;

	@Autowired
	protected DecisionNeededApp decisionNeededApp;

	@Autowired
	protected OpenByIdDialog<ID> openByIdDialog;

	@Autowired
	protected OpenByDateDialog openByDateDialog;

	@Autowired
	protected TextAreaDisplay remarksDisplay;

	@Autowired
	protected AppField<String> createdByDisplay;

	@Autowired
	protected AppField<ZonedDateTime> createdOnDisplay;

	@Autowired
	protected AppGridPane gridPane;

	protected HBox summaryBox, userHBox;

	@Override
	public void actOn(String... id) {
		try {
			if (id.length > 1)
				service.openById(id[0]);
			else
				service.open(id[0]);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	protected List<AppButton> addButtons() {
		return Arrays.asList(//
				newButton.icon("new").tooltip("New entry").build(), //
				backButton.icon("back").tooltip("Previous entry").build(), //
				openByIdButton.icon("openByNo").tooltip("Open an entry").build(), //
				nextButton.icon("next").tooltip("Next entry").build(), //
				saveButton.icon("save").tooltip("Save entry").build());
	}

	protected void clearControl(InputControl<?> control) {
		control.setValue(null);
		((Node) control).requestFocus();
	}

	protected void clearControlAfterShowingErrorDialog(Exception e, InputControl<?> control) {
		showErrorDialog(e);
		clearControl(control);
	}

	@Override
	protected String getFontIcon() {
		return ((Iconed) service).getFontIcon();
	}

	@Override
	protected String getHeaderText() {
		return service.getHeaderText();
	}

	@Override
	protected String getTitleText() {
		return service.getTitleText();
	}

	protected BooleanBinding isPosted() {
		return createdOnDisplay.isNotEmpty();
	}

	protected BooleanBinding notPosted() {
		return isPosted().not();
	}

	protected void open(LocalDate d) {
		try {
			service.open(d);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	protected void openSelected() {
		String id = getDialogInput();
		if (id != null && !id.isEmpty())
			actOn(id);
	}

	protected String getDialogInput() {
		openByIdDialog.header(openByIdDialogHeader()).prompt(openByIdDialogPrompt()).addParent(this).start();
		return openByIdDialog.getId();
	}

	protected String openByIdDialogHeader() {
		return service.getOpenDialogHeader();
	}

	protected String openByIdDialogPrompt() {
		return "Enter ID of " + getHeaderText() + " to open";
	}

	@Override
	public void refresh() {
		updateLogNodes();
		super.refresh();
	}

	protected void reset() {
		((Reset) service).reset();
		refresh();
	}

	protected void save() {
		try {
			service.save();
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	protected void setListeners() {
		setOnCloseRequest(e -> reset());
		newButton.setOnAction(e -> reset());
		backButton.setOnAction(e -> openPrevious());
		nextButton.setOnAction(e -> openNext());
		saveButton.setOnAction(e -> save());
		openByIdButton.setOnAction(e -> openSelected());
	}

	private void openPrevious() {
		try {
			service.previous();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	private void openNext() {
		try {
			service.next();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	protected StackPane stackPane(Node... nodes) {
		StackPane p = new StackPane(nodes);
		p.setAlignment(Pos.CENTER_LEFT);
		return p;
	}

	protected HBox trackedPane() {
		return box.forHorizontalPane(asList(//
				label.name(trackedByLabelName()), createdByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), createdOnDisplay.readOnly().build(TIMESTAMP)));//
	}

	protected String trackedByLabelName() {
		return "Created by";
	}

	protected void updateLogNodes() {
		if (createdByDisplay != null) {
			createdByDisplay.setValue(((CreationTracked) service).getCreatedBy());
			createdOnDisplay.setValue(((CreationTracked) service).getCreatedOn());
		}
	}
}
