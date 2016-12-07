package ph.txtdis.fx.pane;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ph.txtdis.app.CustomerListApp;
import ph.txtdis.dto.Customer;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.FocusRequested;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.service.CustomerIdAndNameService;
import ph.txtdis.service.CustomerSearchableService;

@Lazy
@Component("customerBox")
public class CustomerBox {

	@Autowired
	private AppBoxPaneFactory box;

	@Autowired
	private AppButton customerSearchButton;

	@Autowired
	private AppField<Long> customerIdInput;

	@Autowired
	private AppField<String> customerNameDisplay;

	@Autowired
	private CustomerListApp customerListApp;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private SearchDialog searchDialog;

	private CustomerIdAndNameService service;

	private Stage stage;

	private HBox hbox;

	public void disableIdInputIf(BooleanBinding b) {
		customerIdInput.disableIf(customerNameDisplay.isNotEmpty().or(b));
	}

	public void disableIdInput() {
		customerIdInput.disableProperty().unbind();
		customerIdInput.setDisable(true);
	}

	public HBox get() {
		return hbox;
	}

	public HBox build(Stage stage, CustomerIdAndNameService service) {
		this.stage = stage;
		this.service = service;
		return build();
	}

	private HBox build() {
		customerIdInput.width(140).build(ID);
		customerNameDisplay.readOnly().width(420).build(TEXT);
		buildSearchButton();
		return hbox = box.forHorizontals(customerIdInput, customerNameDisplay, customerSearchButton);
	}

	private void buildSearchButton() {
		customerSearchButton.fontSize(16).icon("search").build();
		customerSearchButton.disableIf(isCustomerIdInputDisabled());
		customerSearchButton.setOnAction(e -> openSearchDialog());
	}

	private ReadOnlyBooleanProperty isCustomerIdInputDisabled() {
		return customerIdInput.disabledProperty();
	}

	private void openSearchDialog() {
		customerIdInput.setValue(null);
		searchDialog.criteria("name").addParent(stage).start();
		String name = searchDialog.getText();
		if (name != null)
			search(name);
	}

	private void search(String name) {
		try {
			((CustomerSearchableService) service).searchForCustomer(name);
			validateSelectedCustomerFromSearchList();
		} catch (Exception e) {
			handleError(e);
		}
	}

	private void validateSelectedCustomerFromSearchList() {
		Customer c = getSelectionFromSearchResults();
		if (c == null)
			return;
		customerIdInput.setValue(c.getId());
		customerIdInput.requestFocus();
	}

	private Customer getSelectionFromSearchResults() {
		customerListApp.addParent(stage).start();
		return customerListApp.getSelection();
	}

	public Long getId() {
		return customerIdInput.getValue();
	}

	public void handleError(Exception e) {
		e.printStackTrace();
		dialog.show(e).addParent(stage).start();
		customerIdInput.setValue(null);
		customerNameDisplay.setValue(null);
		customerIdInput.requestFocus();
	}

	public void hideSearchButton() {
		customerSearchButton.visibleProperty().unbind();
		customerSearchButton.setVisible(false);
	}

	public BooleanBinding isNameDisplayEmpty() {
		return customerNameDisplay.isEmpty();
	}

	public void refresh() {
		customerIdInput.setValue(service.getCustomerId());
		customerNameDisplay.setValue(service.getCustomerName());
	}

	public void requestFocus() {
		customerIdInput.requestFocus();
	}

	public void setFocusAfterCustomerValidation(FocusRequested control) {
		if (customerNameDisplay.isEmpty().get())
			customerIdInput.requestFocus();
		else
			control.requestFocus();
	}

	public void setOnAction(EventHandler<ActionEvent> e) {
		customerIdInput.setOnAction(e);
	}

	public void setSearchButtonVisibleIfNot(BooleanBinding b) {
		customerSearchButton.visibleProperty().bind((isCustomerIdInputDisabled().not().and(b.not())));
	}
}
