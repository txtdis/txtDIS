package ph.txtdis.mgdc.app;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.AbstractRemarkedKeyedApp;
import ph.txtdis.app.PickListApp;
import ph.txtdis.dto.PickList;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.fx.table.PickListTable;
import ph.txtdis.mgdc.service.PickListService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static javafx.beans.binding.Bindings.when;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.type.Type.*;

public abstract class AbstractPickListApp<AS extends PickListService> //
	extends AbstractRemarkedKeyedApp<AS, PickList, Long, Long> //
	implements PickListApp {

	@Autowired
	protected AppCombo<String> assistantCombo, driverCombo, truckCombo;

	@Autowired
	protected PickListTable table;

	@Autowired
	private AppButton printButton;

	@Autowired
	private AppFieldImpl<String> printedByDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> printedOnDisplay;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> c = new ArrayList<>(super.addButtons());
		printButton.icon("print").tooltip("Print...").build();
		c.add(printButton);
		return c;
	}

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), pane.centeredHorizontal(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateStackPane(), 1, 0, 2, 1);
		gridPane.add(label.field("Truck"), 3, 0);
		gridPane.add(truckCombo, 4, 0);
		addPersonCombosToGridpane();
		gridPane.add(label.field("Remarks"), 0, 1);
		gridPane.add(remarksDisplay(), 1, 1, 10, 2);
		return gridPane;
	}

	@Override
	protected HBox trackedPane() {
		HBox hbox = super.trackedPane();
		hbox.getChildren().addAll(printNodes());
		return hbox;
	}

	@Override
	protected Node orderDateStackPane() {
		orderDateDisplay.readOnly().build(DATE);
		return super.orderDateStackPane();
	}

	protected void addPersonCombosToGridpane() {
		gridPane.add(label.field("Driver"), 5, 0);
		gridPane.add(driverCombo, 6, 0);
	}

	private ScrollPane remarksDisplay() {
		remarksDisplay.build();
		remarksDisplay.makeEditable();
		return remarksDisplay.get();
	}

	private List<Node> printNodes() {
		return asList(//
			label.name("Printed by"), printedByDisplay.readOnly().width(120).build(TEXT), //
			label.name("on"), printedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	protected void setBindings() {
		orderDateDisplay.visibleProperty().bind(isPosted() //
			.or(orderDatePicker.visibleProperty().not()));
		orderDatePicker.visibleProperty().bind(isNew());
		driverCombo.disableIf(truckCombo.isEmpty() //
			.or(isPickup()));
		assistantCombo.disableIf(driverCombo.isEmpty());
		remarksDisplay.editableIf(ifPickUpThenOnEmptyDateElseOnEmptySecondPerson());
		printButton.disableIf(isNew() //
			.or(printedByDisplay.isNotEmpty()));
		saveButton.disableIf(table.isEmpty() //
			.or(table.disabled()) //
			.or(isPosted()));
		truckCombo.disableIf(noPickDate());
	}

	protected BooleanBinding isPickup() {
		return truckCombo.is(PICK_UP.toString());
	}

	private BooleanBinding ifPickUpThenOnEmptyDateElseOnEmptySecondPerson() {
		return when(isPickup()) //
			.then(noPickDate()) //
			.otherwise(noSecondPerson());
	}

	private BooleanBinding noPickDate() {
		return orderDatePicker.isEmpty();
	}

	protected BooleanBinding noSecondPerson() {
		return assistantCombo.isEmpty();
	}

	@Override
	public void goToDefaultFocus() {
		orderDatePicker.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		orderDatePicker.onAction(e -> validateDate());
		truckCombo.onAction(e -> createTableContextMenuUponTruckValidation());
		driverCombo.onAction(e -> validateDriver());
		assistantCombo.onAction(e -> validateAssistant());
		printButton.onAction(e -> print());
		table.setOnItemChange(e -> service.setBookings(table.getItems()));
	}

	private void validateDate() {
		if (pickDate() != null)
			try {
				service.setPickDateUponValidation(pickDate());
			} catch (Exception e) {
				renew();
				clearControlAfterShowingErrorDialog(e, orderDatePicker);
			}
	}

	private void createTableContextMenuUponTruckValidation() {
		if (service.isNew() && truckCombo.hasSelectedItem())
			try {
				service.setTruckUponValidation(truckCombo.getValue());
				refreshSucceedingControlsAfterTruckCombo();
				table.addMenu();
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, truckCombo);
			}
	}

	private void validateDriver() {
		if (service.isNew() && driverCombo.hasSelectedItem())
			try {
				service.setDriverUponValidation(driverCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, driverCombo);
			}
	}

	private void validateAssistant() {
		if (service.isNew() && assistantCombo.hasSelectedItem())
			try {
				service.setAssistantUponValidation(assistantCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, assistantCombo);
			}
	}

	private void print() {
		try {
			service.print();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private LocalDate pickDate() {
		return orderDatePicker.getValue();
	}

	@Override
	protected void renew() {
		truckCombo.empty();
		driverCombo.empty();
		assistantCombo.empty();
		super.renew();
	}

	protected void refreshSucceedingControlsAfterTruckCombo() {
		driverCombo.items(service.listDrivers());
		assistantCombo.items(service.listHelpers());
		remarksDisplay.setValue(service.getRemarks());
		table.items(service.getBookings());
		printedByDisplay.setValue(service.getPrintedBy());
		printedOnDisplay.setValue(service.getPrintedOn());
	}

	@Override
	public void refresh() {
		super.refresh();
		orderDateDisplay.setValue(service.getPickDate());
		orderDatePicker.setValue(service.getPickDate());
		truckCombo.items(service.listTrucks());
		refreshSucceedingControlsAfterTruckCombo();
	}
}