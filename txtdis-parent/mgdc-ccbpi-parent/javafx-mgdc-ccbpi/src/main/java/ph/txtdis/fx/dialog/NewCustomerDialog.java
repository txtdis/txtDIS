package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.app.Startable;
import ph.txtdis.dto.Customer;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.CustomerService;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("newCustomerDialog")
public class NewCustomerDialog extends AbstractInputDialog implements Startable {

	@Autowired
	private AppField<Long> idDisplay;

	@Autowired
	private AppField<String> nameField;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private LabelFactory label;

	@Autowired
	private CustomerService service;

	@Autowired
	protected AppButton saveButton;

	private Customer customer;

	private Long vendorId;

	@Override
	protected Button[] buttons() {
		return new Button[] { saveButton(), closeButton() };
	}

	private Button saveButton() {
		saveButton.large("Save").build();
		saveButton.setOnAction(event -> saveUser());
		saveButton.disableIf(nameField.isEmpty());
		return saveButton;
	}

	private void saveUser() {
		try {
			customer = service.save(newCustomer());
			close();
		} catch (Exception e) {
			handleError(e);
		}
	}

	private Customer newCustomer() {
		Customer c = new Customer();
		c.setVendorId(vendorId);
		c.setName(name());
		return c;
	}

	private String name() {
		return nameField.getText();
	}

	private void handleError(Exception e) {
		dialog.show(e).addParent(this).start();
		refresh();
	}

	public Customer getCustomer() {
		return customer;
	}

	@Override
	protected List<Node> nodes() {
		return Arrays.asList(header(), grid(), buttonBox());
	}

	@Override
	public void refresh() {
		idDisplay.setValue(vendorId);
		nameField.clear();
		customer = null;
		super.refresh();
	}

	@Override
	public void setFocus() {
		idDisplay.requestFocus();
	}

	protected AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(label.field("Vendor ID"), 0, 0);
		grid.add(idField(), 1, 0);
		grid.add(label.field("Name"), 0, 1);
		grid.add(nameField(), 1, 1);
		return grid;
	}

	private AppField<Long> idField() {
		idDisplay.readOnly().build(Type.ID);
		idDisplay.setValue(vendorId);
		return idDisplay;
	}

	private AppField<String> nameField() {
		nameField.build(TEXT);
		nameField.setOnAction(event -> validateName());
		return nameField;
	}

	private void validateName() {
		try {
			validateName(nameField.getText());
		} catch (Exception e) {
			dialog.show(e).addParent(this).start();
			refresh();
		}
	}

	private void validateName(String name) throws Exception {
		if (service.findByName(name) != null)
			throw new DuplicateException(name);
	}

	@Override
	protected String headerText() {
		return "Add New Customer";
	}

	public NewCustomerDialog vendorId(Long id) {
		vendorId = id;
		return this;
	}
}