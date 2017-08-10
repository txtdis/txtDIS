package ph.txtdis.mgdc.ccbpi.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.ccbpi.service.CokeCustomerService;
import ph.txtdis.type.Type;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("customerDialog")
public class CustomerDialogImpl //
	extends AbstractInputDialog //
	implements CustomerDialog {

	@Autowired
	private AppCombo<String> routeCombo, deliveryCombo;

	@Autowired
	private AppFieldImpl<Long> idDisplay;

	@Autowired
	private AppFieldImpl<String> nameField;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private CokeCustomerService service;

	private Customer customer;

	private Long vendorId;

	private String name;

	@Override
	protected List<AppButton> buttons() {
		return asList(saveButton(), closeButton());
	}

	private AppButton saveButton() {
		AppButton saveButton = button.large("Save").build();
		saveButton.onAction(event -> save());
		saveButton.disableIf(nameField.isEmpty());
		return saveButton;
	}

	private void save() {
		try {
			customer = saveCustomer();
			close();
		} catch (Exception e) {
			handleError(e);
		}
	}

	private Customer saveCustomer() throws Exception {
		if (customer == null)
			return null;
		return service.save(customer, vendorId, nameField.getText(), routeCombo.getValue(), deliveryCombo.getValue());
	}

	private void handleError(Exception e) {
		showErrorDialog(e);
		refresh();
	}

	@Override
	public void refresh() {
		idDisplay.setValue(vendorId);
		refreshName();
		refreshRoute();
		customer = null;
		super.refresh();
	}

	private void refreshName() {
		if (name != null) {
			nameField.setValue(name);
			nameField.readOnly();
		}
		else
			nameField.clear();

	}

	private void refreshRoute() {
		if (name != null && customer != null && customer.getChannel() != null)
			routeCombo.select(customer.getChannel().getName());
	}

	@Override
	public Customer getCustomer() {
		return customer;
	}

	@Override
	public void goToDefaultFocus() {
		idDisplay.requestFocus();
	}

	@Override
	protected List<Node> nodes() {
		return asList(header(), grid(), buttonBox());
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(label.field("Vendor ID"), 0, 0);
		grid.add(idField(), 1, 0);
		grid.add(label.field("Name"), 0, 1);
		grid.add(nameField(), 1, 1);
		grid.add(label.field("Route"), 0, 2);
		grid.add(routeCombo(), 1, 2);
		grid.add(label.field("Assigned to"), 0, 3);
		grid.add(deliveryCombo(), 1, 3);
		return grid;
	}

	private AppFieldImpl<Long> idField() {
		idDisplay.readOnly().build(Type.ID);
		idDisplay.setValue(vendorId);
		return idDisplay;
	}

	private AppFieldImpl<String> nameField() {
		nameField.build(TEXT);
		nameField.onAction(event -> validateName());
		return nameField;
	}

	private Node routeCombo() {
		routeCombo.items(service.listRoutes());
		return routeCombo;
	}

	private Node deliveryCombo() {
		deliveryCombo.items(service.listTruckRoutes());
		return deliveryCombo;
	}

	private void validateName() {
		try {
			validateName(nameField.getText());
		} catch (Exception e) {
			showErrorDialog(e);
			refresh();
		}
	}

	private void validateName(String name) throws Exception {
		Customer customer = service.findByName(name);
		if (customer != null)
			throw new DuplicateException(name);
	}

	@Override
	protected String headerText() {
		return "Add/Update Outlet";
	}

	@Override
	protected void nullData() {
		super.nullData();
		customer = null;
		vendorId = null;
		name = null;
	}

	@Override
	public CustomerDialog outletName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public CustomerDialog vendorId(Long id) {
		vendorId = id;
		return this;
	}
}