package ph.txtdis.mgdc.gsm.fx.pane;

import static java.util.Arrays.asList;
import static javafx.beans.binding.Bindings.when;
import static javafx.geometry.Pos.CENTER_RIGHT;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.ErrorHandling;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.pane.AppBoxPaneFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.pane.CustomerPane;
import ph.txtdis.mgdc.fx.table.RoutingTable;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.fx.table.VisitScheduleTable;
import ph.txtdis.mgdc.gsm.service.CreditedAndDiscountedCustomerService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;

public abstract class AbstractCustomerPane //
		extends Pane //
		implements CustomerPane {

	@Autowired
	private AppBoxPaneFactory box;

	@Autowired
	private CreditedAndDiscountedCustomerService service;

	@Autowired
	private LabelFactory label;

	@Autowired
	private AppFieldImpl<Long> idField;

	@Autowired
	private AppFieldImpl<String> parentDisplay, streetField;

	@Autowired
	private AppCombo<Location> provinceCombo, cityCombo;

	@Autowired
	private AppCombo<VisitFrequency> visitCombo;

	@Autowired
	private AppGridPane gridPane;

	@Autowired
	protected AppCombo<Channel> channelCombo;

	@Autowired
	protected AppCombo<Location> barangayCombo;

	@Autowired
	protected AppCombo<PartnerType> typeCombo;

	@Autowired
	protected AppFieldImpl<Long> parentIdField;

	@Autowired
	protected AppFieldImpl<String> nameField;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private RoutingTable routingTable;

	@Autowired
	private VisitScheduleTable scheduleTable;

	@Override
	public void clear() {
		routingTable.removeListener();
		scheduleTable.removeListener();
	}

	@Override
	public void disableNameFieldIf(ObservableBooleanValue b) {
		nameField.disableIf(b);
	}

	@Override
	public BooleanBinding hasIncompleteData() {
		return typeCombo.isEmpty().or(showsPartnerAsACustomer().and(routingTable.isEmpty()));
	}

	@Override
	public List<Node> mainVerticalPaneNodes() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("ID No."), 0, 0);
		gridPane.add(customerBox(), 1, 0, 4, 1);
		gridPane.add(parentBox(), 6, 0, 2, 1);

		gridPane.add(label.field("Street"), 0, 1);
		gridPane.add(streetField.build(TEXT), 1, 1, 4, 1);
		gridPane.add(parentDisplay.readOnly().build(TEXT), 6, 1, 2, 1);

		gridPane.add(label.field("Province"), 0, 2);
		gridPane.add(provinceCombo.width(180).noAutoSelectSingleItem().items(service.listProvinces()), 1, 2, 2, 1);
		gridPane.add(label.field("City/Town"), 3, 2);
		gridPane.add(cityCombo.width(200), 4, 2, 2, 1);
		gridPane.add(label.field("Barangay"), 6, 2);
		gridPane.add(barangayCombo.width(280), 7, 2);

		gridPane.add(label.field("Type"), 0, 3);
		gridPane.add(typeCombo, 1, 3, 2, 1);
		gridPane.add(label.field("Channel"), 3, 3);
		gridPane.add(channelCombo.items(service.listChannels()), 4, 3);
		gridPane.add(label.field("Visit per Month"), 5, 3, 2, 1);
		gridPane.add(visitCombo.items(VisitFrequency.values()), 7, 3);

		return asList(gridPane, tableBox());
	}

	private Node customerBox() {
		idField.readOnly().build(ID);
		nameField.build(TEXT);
		return box.forIdName(idField, label.name("Name"), nameField);
	}

	private Node parentBox() {
		HBox h = box.forIdName(label.name("Parent / Former ID No."), parentIdField.build(ID));
		h.setAlignment(CENTER_RIGHT);
		return h;
	}

	private HBox tableBox() {
		return box.forHorizontalPane(routeTablePane(), scheduleTablePane());
	}

	private VBox routeTablePane() {
		return box.forVerticals(label.group("Route Assignment"), routingTable.build());
	}

	private VBox scheduleTablePane() {
		return box.forVerticals(label.group("Visit Schedule"), scheduleTable.build());
	}

	@Override
	public void refresh() {
		idField.setValue(service.getId());
		nameField.setText(service.getName());
		parentIdField.setValue(service.getParentId());
		parentDisplay.setValue(service.getParentName());
		streetField.setText(service.getStreet());
		provinceCombo.select(service.getProvince());
		cityCombo.select(service.getCity());
		barangayCombo.select(service.getBarangay());
		typeCombo.items(service.listTypes());
		visitCombo.select(service.getVisitFrequency());
		refreshRoutingAndChannel();
		refreshScheduleTableItem();
	}

	private void refreshRoutingAndChannel() {
		channelCombo.select(service.getChannel());
		routingTable.items(service.getRouteHistory());
	}

	private void refreshScheduleTableItem() {
		Channel c = channelCombo.getValue();
		List<WeeklyVisit> vs = service.getVisitSchedule(c);
		scheduleTable.items(vs);
	}

	@Override
	public void save() {
		setNameAndLocation();
		service.setType(typeCombo.getValue());
		setPropertiesIfAnOutlet();
	}

	private void setNameAndLocation() {
		service.setName(nameField.getText());
		service.setStreet(streetField.getText());
		service.setProvince(provinceCombo.getValue());
		service.setCity(cityCombo.getValue());
		service.setBarangay(barangayCombo.getValue());
	}

	private void setPropertiesIfAnOutlet() {
		if (typeCombo.getValue() != OUTLET)
			return;
		service.setChannel(channelCombo.getValue());
		service.setVisitFrequency(visitCombo.getValue());
		service.setRouteHistory(routingTable.getItems());
		service.setVisitSchedule(scheduleTable.getItems());
	}

	@Override
	public void setBindings() {
		streetField.disableIf(nameField.isEmpty());
		parentIdField.disableIf(nameField.isEmpty());
		provinceCombo.disableIf(streetField.isEmpty());
		cityCombo.disableIf(provinceCombo.isEmpty());
		barangayCombo.disableIf(cityCombo.isEmpty());
		typeCombo.disableIf(barangayCombo.isEmpty());
		channelCombo.disableIf(typeCombo.isEmpty());
		visitCombo.disableIf(channelCombo.isEmpty() //
				.or(notVisitedChannel()));
		routingTable.disableIf(when(notVisitedChannel()) //
				.then(channelCombo.isEmpty()) //
				.otherwise(visitCombo.isEmpty()));
		scheduleTable.disableIf(routingTable.isEmpty() //
				.or(channelCombo.disabledProperty()) //
				.or(notVisitedChannel()));
	}

	private BooleanBinding notVisitedChannel() {
		return channelCombo.are(service.listVisitedChannels()).not();
	}

	@Override
	public void setFocus() {
		nameField.requestFocus();
	}

	@Override
	public void select() {
		if (service.isNew())
			nameField.requestFocus();
	}

	@Override
	public BooleanBinding showsPartnerAsACustomer() {
		return typeCombo.is(OUTLET);
	}

	private void handleError(ErrorHandling control, Exception e) {
		dialog.show(e).addParent(gridPane).start();
		control.handleError();
	}

	@Override
	public void setListeners() {
		nameField.onAction(e -> validateName());
		parentIdField.onAction(e -> validateParent());
		provinceCombo.onAction(e -> setCityComboItemsAfterClearingTypeCombo());
		cityCombo.onAction(e -> setBarangayComboItemsAfterClearingTypeCombo());
		barangayCombo.onAction(e -> clearTypeCombo());
		typeCombo.onAction(e -> proceedPerSelectedType());
		channelCombo.onAction(e -> refreshScheduleTableItem());
		scheduleTable.setOnItemCheckBoxSelectionChange((items, old, changed) -> verifyUserAllowedToChangeSchedule(old, changed));
	}

	private void validateName() {
		try {
			service.setNameUponValidation(nameField.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			handleError(nameField, e);
		} finally {
			refresh();
		}
	}

	private void validateParent() {
		if (parentIdField.getValue() != 0L)
			try {
				service.setParentIfExists(parentIdField.getValue());
				parentDisplay.setValue(service.getParentName());
			} catch (Exception e) {
				parentDisplay.clear();
				handleError(parentIdField, e);
			}
	}

	private void setCityComboItemsAfterClearingTypeCombo() {
		if (service.isNew())
			clearTypeCombo();
		setCityComboItems();
	}

	private void clearTypeCombo() {
		if (service.isNew())
			typeCombo.clear();
	}

	private void setCityComboItems() {
		Location p = provinceCombo.getValue();
		List<Location> l = service.listCities(p);
		cityCombo.items(l);
	}

	private void setBarangayComboItemsAfterClearingTypeCombo() {
		if (service.isNew())
			clearTypeCombo();
		setBarangayComboItems();
	}

	private void setBarangayComboItems() {
		Location c = cityCombo.getValue();
		List<Location> l = service.listBarangays(c);
		barangayCombo.items(l);
	}

	private void proceedPerSelectedType() {
		if (!service.isNew())
			return;
		clearControlsAfterPartnerTypeCombo();
		setRouteAsPickUpAndChannelAsWarehouseSalesIfTypeIsInternal();
	}

	private void clearControlsAfterPartnerTypeCombo() {
		channelCombo.clear();
		visitCombo.clear();
		routingTable.getItems().clear();
		scheduleTable.getItems().clear();
	}

	private void setRouteAsPickUpAndChannelAsWarehouseSalesIfTypeIsInternal() {
		try {
			setRouteAsPickUpAndChannelAsWarehouseSales();
		} catch (Exception e) {
			e.printStackTrace();
			handleError(typeCombo, e);
		}
	}

	private void setRouteAsPickUpAndChannelAsWarehouseSales() throws Exception {
		if (typeCombo.getValue() != PartnerType.INTERNAL)
			return;
		service.setRouteAsPickUpAndChannelAsWarehouseSales();
		refreshRoutingAndChannel();
	}

	private void verifyUserAllowedToChangeSchedule(ObservableList<WeeklyVisit> old, ObservableList<WeeklyVisit> changed) {
		if (!service.isNew() && old != null && changed != null)
			try {
				service.verifyUserIsAllowedToChangeSchedule(old, changed);
			} catch (Exception e) {
				dialog.show(e).addParent(gridPane).start();
				refreshScheduleTableItem();
			}
	}
}
