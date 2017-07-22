package ph.txtdis.mgdc.gsm.fx.pane;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.FocusRequested;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.AppBoxPaneFactory;
import ph.txtdis.mgdc.gsm.app.CustomerListApp;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.CustomerIdAndNameService;
import ph.txtdis.service.CustomerSearchableService;

@Scope("prototype")
@Component("customerBox")
public class CustomerBox {

	@Autowired
	private AppBoxPaneFactory box;

	@Autowired
	private AppButtonImpl customerSearchButton;

	@Autowired
	private AppFieldImpl<Long> customerIdInput;

	@Autowired
	private AppFieldImpl<String> customerNameDisplay;

	@Autowired
	private CustomerListApp customerListApp;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private SearchDialog searchDialog;

	private CustomerIdAndNameService service;

	private Stage stage;

	private HBox hbox;

	public void disableIdInputIf(ObservableBooleanValue b) {
		customerIdInput.disableProperty().unbind();
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
		customerSearchButton.onAction(e -> openSearchDialog());
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

	public BooleanBinding isEmpty() {
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

	public void onAction(EventHandler<ActionEvent> e) {
		customerIdInput.onAction(e);
	}

	public void setId(Long id) {
		customerIdInput.setValue(id);
	}

	public void setSearchButtonVisibleIfNot(BooleanBinding b) {
		customerSearchButton.visibleProperty().bind((isCustomerIdInputDisabled().not().and(b.not())));
	}
}
