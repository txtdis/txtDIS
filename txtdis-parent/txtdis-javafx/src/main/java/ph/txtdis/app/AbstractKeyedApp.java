package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ph.txtdis.dto.CreationLogged;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.InputControl;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.dialog.OpenByIdDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.service.IconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.type.Type;

public abstract class AbstractKeyedApp< //
		AS extends SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK>, //
		T extends Keyed<PK>, PK, ID> //
		extends AbstractApp<AS> //
		implements LaunchableApp {

	@Autowired
	protected AppButtonImpl decisionButton, newButton, backButton, openByIdButton, nextButton, saveButton;

	@Autowired
	protected AppFieldImpl<LocalDate> orderDateDisplay;

	@Autowired
	protected AppFieldImpl<String> createdByDisplay;

	@Autowired
	protected AppFieldImpl<ZonedDateTime> createdOnDisplay;

	@Autowired
	protected AppGridPane gridPane;

	@Autowired
	protected DecisionNeededApp decisionNeededApp;

	@Autowired
	protected LocalDatePicker orderDatePicker;

	@Autowired
	protected OpenByIdDialog<ID> openByIdDialog;

	protected HBox summaryBox, userHBox;

	@Override
	public void actOn(String... keys) {
		try {
			actBasedOnKeySource(keys);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	private void actBasedOnKeySource(String[] keys) throws Exception {
		if (isFromDoubleClickedTableItem(keys))
			service.openByDoubleClickedTableCellKey(keys[0]);
		else
			service.openByOpenDialogInputtedKey(keys[0]);
	}

	@Override
	protected List<AppButtonImpl> addButtons() {
		return asList( //
				newButton.icon("new").tooltip("New entry").build(), //
				backButton.icon("back").tooltip("Previous entry").build(), //
				openByIdButton.icon("openByNo").tooltip("Open an entry").build(), //
				nextButton.icon("next").tooltip("Next entry").build(), //
				saveButton.icon("save").tooltip("Save entry").build());
	}

	protected void buttonListeners() {
		newButton.onAction(e -> renew());
		backButton.onAction(e -> openPrevious());
		nextButton.onAction(e -> openNext());
		saveButton.onAction(e -> save());
		openByIdButton.onAction(e -> openSelected());
	}

	protected void clearControl(InputControl<?> control) {
		control.setValue(null);
		((Node) control).requestFocus();
	}

	protected void clearControlAfterShowingErrorDialog(Exception e, InputControl<?> control) {
		showErrorDialog(e);
		clearControl(control);
	}

	protected void comboAndInputGridNodes( //
			String comboLabel, //
			AppCombo<String> stringCombo, //
			String inputLabel, //
			Node inputNode, //
			int row, //
			int comboColumnSpan) {
		labelGridNode(comboLabel, 0, row);
		gridPane.add(stringCombo, 1, row, comboColumnSpan, 1);
		labelGridNode(inputLabel, ++comboColumnSpan, row);
		gridPane.add(inputNode, ++comboColumnSpan, row);
	}

	protected void currencyDisplayGridNodes(//
			String name, //
			AppField<BigDecimal> displayField, //
			int width, //
			int column, //
			int row) {
		currencyInputGridNodes(name, displayField, width, column, row);
		displayField.disable();
	}

	protected void currencyInputGridNodes(//
			String name, //
			AppField<BigDecimal> inputField, //
			int width, //
			int column, //
			int row) {
		labelGridNode(name, column, row);
		gridPane.add(inputField.width(width).build(CURRENCY), ++column, row);
	}

	protected void dateGridNodes( //
			String dateLabel, //
			AppField<LocalDate> dateDisplay, //
			LocalDatePicker datePicker, //
			int column, //
			int row, //
			int dateStackPaneColumnSpan) {
		labelGridNode(dateLabel, column, row);
		gridPane.add(dateStackPane(dateDisplay, datePicker), ++column, row, dateStackPaneColumnSpan, 1);
	}

	private Node dateStackPane(AppField<LocalDate> dateDisplay, LocalDatePicker datePicker) {
		return stackPane( //
				dateDisplay.readOnly().build(DATE), //
				datePicker);
	}

	protected void dateDisplayGridNodes( //
			String name, //
			AppField<LocalDate> displayField, //
			int column, //
			int row) {
		labelGridNode(name, column, row);
		gridPane.add(displayField.readOnly().width(110).build(DATE), ++column, row);
	}

	protected String getDialogInput() {
		openByIdDialog //
				.header(service.getOpenDialogHeader()) //
				.prompt(service.getOpenDialogPrompt()) //
				.addParent(this).start();
		return openByIdDialog.getKey();
	}

	@Override
	protected String getFontIcon() {
		return ((IconAndModuleNamedAndTypeMappedService) service).getIconName();
	}

	@Override
	protected String getHeaderText() {
		return service.getHeaderName();
	}

	@Override
	protected String getTitleText() {
		return service.getTitleName();
	}

	protected void handleError(Exception e, InputControl<?>... controls) {
		showErrorDialog(e);
		setControlValuesToNull(controls);
		((Node) controls[0]).requestFocus();
	}

	private boolean isFromDoubleClickedTableItem(String[] keys) {
		return keys.length > 1;
	}

	protected BooleanBinding isNew() {
		return createdOnDisplay.isEmpty();
	}

	protected BooleanBinding isPosted() {
		return isNew().not();
	}

	protected void labelGridNode(String name, int column, int row) {
		labelGridNode(name, column, row, 1);
	}

	protected void labelGridNode(String name, int column, int row, int columnSpan) {
		gridPane.add(label.field(name), column, row, columnSpan, 1);
	}

	protected List<Node> logNodes( //
			String actedBy, //
			AppFieldImpl<String> actedByDisplay, //
			AppFieldImpl<ZonedDateTime> actedOnDisplay) {
		return asList(//
				label.name(actedBy), actedByDisplay.readOnly().width(110).build(TEXT), //
				label.name("on"), actedOnDisplay.readOnly().build(TIMESTAMP));
	}

	protected void openNext() {
		try {
			service.next();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	protected void openPrevious() {
		try {
			service.previous();
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

	protected Node orderDateStackPane() {
		return stackPane(orderDateDisplay, orderDatePicker);
	}

	protected void qtyDisplayGridNodes( //
			String name, //
			AppField<BigDecimal> displayField, //
			int column, //
			int row) {
		qtyDisplayGridNodes(name, displayField, column, row, 1);
	}

	protected void qtyDisplayGridNodes( //
			String name, //
			AppField<BigDecimal> displayField, //
			int column, //
			int row, //
			int labelColumnSpan) {
		labelGridNode(name, column, row, labelColumnSpan);
		gridPane.add(displayField.readOnly().width(110).build(QUANTITY), column + labelColumnSpan, row);
	}

	@Override
	public void refresh() {
		refreshLogNodes();
		super.refresh();
	}

	protected void refreshLogNodes() {
		if (createdByDisplay != null) {
			createdByDisplay.setValue(((CreationLogged) service).getCreatedBy());
			createdOnDisplay.setValue(((CreationLogged) service).getCreatedOn());
		}
	}

	protected void save() {
		try {
			service.save();
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	private void setControlValuesToNull(InputControl<?>... controls) {
		for (InputControl<?> c : controls)
			c.setValue(null);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		buttonListeners();
	}

	protected void idDisplayGridNodes( //
			String name, //
			AppField<Long> displayField, //
			int column, //
			int row) {
		labelGridNode(name, column, row);
		gridPane.add(displayField.readOnly().width(110).build(Type.ID), ++column, row);
	}

	protected void orderDateBinding() {
		orderDateDisplay.visibleProperty().bind(isPosted() //
				.or(orderDatePicker.disabled()) //
				.or(orderDatePicker.visibleProperty().not()));
		orderDatePicker.visibleProperty().bind(isPosted().not());
	}

	protected StackPane stackPane(Node... nodes) {
		StackPane p = new StackPane(nodes);
		p.setAlignment(Pos.CENTER_LEFT);
		return p;
	}

	protected void textDisplayGridNodes( //
			String name, //
			AppField<String> displayField, //
			int width, //
			int column, //
			int row, //
			int displayFieldColumnSpan) {
		labelGridNode(name, column, row);
		gridPane.add(displayField.readOnly().width(width).build(TEXT), ++column, row, displayFieldColumnSpan, 1);
	}

	protected void textInputGridNodes( //
			String name, //
			AppField<String> inputField, //
			int column, //
			int row) {
		labelGridNode(name, column, row);
		gridPane.add(inputField.width(110).build(TEXT), ++column, row);
	}

	protected void timestampDisplayGridNodes( //
			String name, //
			AppField<ZonedDateTime> displayField, //
			int column, //
			int row, //
			int colspan) {
		labelGridNode(name, column, row);
		gridPane.add(displayField.readOnly().build(TIMESTAMP), ++column, row, colspan, 1);
	}

	protected HBox trackedPane() {
		return box.forHorizontalPane(creationNodes());//
	}

	protected List<Node> creationNodes() {
		return logNodes(createdByLabelName(), createdByDisplay, createdOnDisplay);
	}

	protected String createdByLabelName() {
		return "Created by";
	}
}
