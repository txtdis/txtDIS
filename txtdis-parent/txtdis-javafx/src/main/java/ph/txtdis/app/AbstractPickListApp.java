package ph.txtdis.app;

import static java.util.Arrays.asList;
import static javafx.beans.binding.Bindings.when;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.PickList;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.PickListTable;
import ph.txtdis.service.PickListService;

public abstract class AbstractPickListApp<AS extends PickListService> extends AbstractIdApp<AS, PickList, Long, Long>
		implements PickListApp {

	@Autowired
	private AppButton printButton;

	@Autowired
	private AppCombo<String> truckCombo;

	@Autowired
	private AppField<LocalDate> pickDateDisplay;

	@Autowired
	private AppField<String> remarksInput, printedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> printedOnDisplay;

	@Autowired
	private LocalDatePicker pickDatePicker;

	@Autowired
	private PickListTable table;

	@Autowired
	protected AppCombo<String> driverCombo, helperCombo;

	@Autowired
	protected AppGridPane gridPane;

	@Autowired
	protected LabelFactory label;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> c = new ArrayList<>(super.addButtons());
		printButton.icon("print").tooltip("Print...").build();
		c.add(printButton);
		return c;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), box.forHorizontalPane(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(pickDateNode(), 1, 0, 2, 1);
		gridPane.add(label.field("Truck"), 3, 0);
		gridPane.add(truckCombo, 4, 0);
		gridPane.add(label.field("Driver"), 5, 0);
		gridPane.add(driverCombo, 6, 0);
		addPersonCombosToGridpane();
		gridPane.add(label.field("Remarks"), 0, 1);
		gridPane.add(remarksInput.build(TEXT), 1, 1, 10, 1);
		return gridPane;
	}

	private Node pickDateNode() {
		return stackPane(pickDateDisplay.readOnly().build(DATE), pickDatePicker);
	}

	protected abstract void addPersonCombosToGridpane();

	@Override
	protected HBox trackedPane() {
		HBox hbox = super.trackedPane();
		hbox.getChildren().addAll(printNodes());
		return hbox;
	}

	private List<Node> printNodes() {
		return asList(//
				label.name("Printed by"), printedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), printedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	public void refresh() {
		super.refresh();
		pickDateDisplay.setValue(service.getPickDate());
		pickDatePicker.setValue(service.getPickDate());
		truckCombo.items(service.listTrucks());
		refreshSucceedingControls();
	}

	protected void refreshSucceedingControls() {
		driverCombo.items(service.listDrivers());
		helperCombo.items(service.listHelpers());
		helperCombo.select(service.getHelper());
		remarksInput.setValue(service.getRemarks());
		table.items(service.getBookings());
		printedByDisplay.setValue(service.getPrintedBy());
		printedOnDisplay.setValue(service.getPrintedOn());
	}

	@Override
	protected void reset() {
		truckCombo.empty();
		driverCombo.empty();
		helperCombo.empty();
		super.reset();
	}

	@Override
	protected void setBindings() {
		pickDateDisplay.visibleProperty().bind(isPosted() //
				.or(pickDatePicker.visibleProperty().not()));
		pickDatePicker.visibleProperty().bind(isPosted().not());
		driverCombo.disableIf(truckCombo.isEmpty()//
				.or(isPickup()));
		helperCombo.disableIf(driverCombo.isEmpty());
		remarksInput.disableIf(ifPickUpThenOnEmptyDateElseOnEmptySecondPerson());
		printButton.disableIf(isPosted().not()//
				.or(printedByDisplay.isNotEmpty()));
		saveButton.disableIf(table.isEmpty()//
				.or(table.disabledProperty())//
				.or(isPosted()));
		table.disableIf(remarksInput.disableProperty());
		truckCombo.disableIf(noPickDate());
	}

	private BooleanBinding isPickup() {
		return truckCombo.is("PICK-UP");
	}

	private BooleanBinding ifPickUpThenOnEmptyDateElseOnEmptySecondPerson() {
		return when(isPickup())//
				.then(noPickDate())//
				.otherwise(noSecondPerson());
	}

	protected BooleanBinding noSecondPerson() {
		return helperCombo.isEmpty();
	}

	private BooleanBinding noPickDate() {
		return pickDatePicker.valueProperty().isNull();
	}

	@Override
	public void setFocus() {
		pickDatePicker.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		pickDatePicker.setOnAction(e -> validateDate());
		truckCombo.setOnAction(e -> createTableContextMenuUponTruckValidation());
		driverCombo.setOnAction(e -> validateDriver());
		helperCombo.setOnAction(e -> validateHelper());
		remarksInput.setOnAction(e -> setRemarks());
		printButton.setOnAction(e -> print());
	}

	private void validateDate() {
		if (!noPickDate().get())
			try {
				service.setPickDateUponValidation(pickDatePicker.getValue());
			} catch (Exception e) {
				service.reset();
				refresh();
				clearControlAfterShowingErrorDialog(e, pickDatePicker);
			}
	}

	private void createTableContextMenuUponTruckValidation() {
		if (truckCombo.getValue() != null && truckCombo.hasItems())
			try {
				service.setTruckUponValidation(truckCombo.getValue());
				refreshSucceedingControls();
				table.addMenu();
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, truckCombo);
			}
	}

	private void validateDriver() {
		if (driverCombo.getValue() != null && driverCombo.getItems() != null && driverCombo.getItems().size() > 1)
			try {
				service.setDriverUponValidation(driverCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, driverCombo);
			}
	}

	private void validateHelper() {
		if (helperCombo.getValue() != null && helperCombo.getItems() != null && helperCombo.getItems().size() > 1)
			try {
				service.setHelperUponValidation(helperCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, helperCombo);
			}
	}

	private void setRemarks() {
		String s = remarksInput.getValue();
		if (!s.isEmpty())
			service.setRemarks(s);
	}

	private void print() {
		try {
			service.print();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}
}