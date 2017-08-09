package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.gsm.app.CustomerListAppImpl;
import ph.txtdis.mgdc.gsm.service.CreditedAndDiscountedCustomerService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;

@Scope("prototype")
@Component("filterByCustomerDialog")
public class FilterByCustomerDialog
	extends AbstractInputDialog {

	@Autowired
	private AppFieldImpl<Long> idField;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private CustomerListAppImpl customerListApp;

	@Autowired
	private CreditedAndDiscountedCustomerService customerService;

	private long id;

	public long getId() {
		return id;
	}

	@Override
	public void goToDefaultFocus() {
		idField.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(openButton(), closeButton());
	}

	private AppButton openButton() {
		AppButton openButton = button.large("Filter").build();
		openButton.onAction(event -> setEnteredId());
		openButton.disableIf(idField.isEmpty());
		return openButton;
	}

	private void setEnteredId() {
		id = idField.getValue();
		idField.setValue(null);
		close();
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Customer No."), 0, 0);
		grid.add(customerBox(), 1, 0);
		return asList(header(), grid, buttonBox());
	}

	private HBox customerBox() {
		return pane.horizontal(idField.build(ID), searchButton());
	}

	private Button searchButton() {
		AppButton searchButton = button.fontSize(16).icon("search").build();
		searchButton.focusTraversableProperty().bind(idField.isEmpty());
		searchButton.onAction(event -> openSearchDialog());
		return searchButton;
	}

	private void openSearchDialog() {
		idField.setValue(null);
		SearchDialog searchDialog = searchDialog();
		searchDialog.criteria("name").addParent(this).start();
		String name = searchDialog.getText();
		if (name != null)
			idField.setValue(search(name));
	}

	@Lookup
	SearchDialog searchDialog() {
		return null;
	}

	private Long search(String name) {
		try {
			customerService.search(name);
			customerListApp.addParent(this).start();
			return customerListApp.getSelection().getId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void nullData() {
		super.nullData();
		id = 0L;
	}
}
