package ph.txtdis.mgdc.gsm.fx.dialog;

import static ph.txtdis.type.Type.ID;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.gsm.app.CustomerListAppImpl;
import ph.txtdis.mgdc.gsm.service.CreditedAndDiscountedCustomerService;

@Scope("prototype")
@Component("filterByCustomerDialog")
public class FilterByCustomerDialog extends AbstractInputDialog {

	@Autowired
	private AppButtonImpl openButton, searchButton;

	@Autowired
	private AppFieldImpl<Long> idField;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private CustomerListAppImpl customerListApp;

	@Autowired
	private CreditedAndDiscountedCustomerService customerService;

	@Autowired
	private LabelFactory label;

	@Autowired
	private SearchDialog searchDialog;

	private long id;

	public long getId() {
		return id;
	}

	@Override
	public void goToDefaultFocus() {
		idField.requestFocus();
	}

	private Button openButton() {
		openButton.large("Filter").build();
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
	protected Button[] buttons() {
		return new Button[] { openButton(), closeButton() };
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Customer No."), 0, 0);
		grid.add(customerBox(), 1, 0);
		return Arrays.asList(header(), grid, buttonBox());
	}

	private HBox customerBox() {
		return new HBox(idField.build(ID), searchButton());
	}

	private Button searchButton() {
		searchButton.fontSize(16).icon("search").build();
		searchButton.focusTraversableProperty().bind(idField.isEmpty());
		searchButton.onAction(event -> openSearchDialog());
		return searchButton;
	}

	private void openSearchDialog() {
		idField.setValue(null);
		searchDialog.criteria("name").addParent(this).start();
		String name = searchDialog.getText();
		if (name != null)
			idField.setValue(search(name));
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
