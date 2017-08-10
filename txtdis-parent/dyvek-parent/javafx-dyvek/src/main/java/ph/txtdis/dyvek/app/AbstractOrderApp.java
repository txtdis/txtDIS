package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import ph.txtdis.app.AbstractRemarkedKeyedApp;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.AppGridPane;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractOrderApp<
	LA extends OrderListApp,
	S extends OrderService>
	extends AbstractRemarkedKeyedApp<S, Billable, Long, Long> {

	@Autowired
	protected AppCombo<String> customerCombo, itemCombo;

	@Autowired
	protected AppField<BigDecimal> qtyInput;

	@Autowired
	protected AppField<String> closedByDisplay, orderNoInput;

	@Autowired
	protected AppField<ZonedDateTime> closedOnDisplay;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.remove(openByIdButton);
		b.add(2, searchButton());
		return b;
	}

	private AppButton searchButton() {
		AppButton b = button.icon("search").tooltip("Search...").build();
		b.onAction(e -> openSearchDialog());
		return b;
	}

	private void openSearchDialog() {
		SearchDialog dialog = searchDialog();
		dialog.criteria(service.getAlternateName() + " No.").addParent(this).start();
		search(dialog.getText());
	}

	@Lookup
	SearchDialog searchDialog() {
		return null;
	}

	private void search(String name) {
		if (name != null)
			try {
				service.search(name);
				listSearchResults();
				refresh();
			} catch (Exception e) {
				showErrorDialog(e);
			}
	}

	private void listSearchResults() throws Exception {
		LA app = orderListApp();
		app.addParent(this).start();
		Long id = app.getSelectedKey();
		if (id != null)
			actOn(id.toString(), "");
	}

	protected abstract LA orderListApp();

	@Override
	public void refresh() {
		refreshOrderNo();
		refreshDateDisplay();
		refreshDatePicker();
		refreshItem();
		refreshQty();
		refreshRemarks();
		super.refresh();
	}

	protected void refreshOrderNo() {
		if (orderNoInput != null)
			orderNoInput.setValue(service.getOrderNo());
	}

	protected void refreshDateDisplay() {
		if (orderDateDisplay != null)
			orderDateDisplay.setValue(service.getOrderDate());
	}

	protected void refreshDatePicker() {
		if (orderDatePicker != null)
			orderDatePicker.setValue(service.getOrderDate());
	}

	private void refreshItem() {
		if (itemCombo != null)
			itemCombo.items(service.listItems());
	}

	private void refreshQty() {
		if (qtyInput != null)
			qtyInput.setValue(service.getQty());
	}

	private void refreshRemarks() {
		if (remarksDisplay != null)
			remarksDisplay.setValue(service.getRemarks());
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), trackedPane());
	}

	AppGridPane gridPane() {
		gridPane.getChildren().clear();
		firstGridLine();
		secondGridLine();
		thirdGridLine();
		return gridPane;
	}

	protected abstract void firstGridLine();

	protected abstract void secondGridLine();

	protected void thirdGridLine() {
		remarksGridNodes(2, 11);
	}

	@Override
	protected void renew() {
		super.renew();
		customerCombo.requestFocus();
	}

	@Override
	protected void setBindings() {
		orderNoBinding();
		orderDateBinding();
		itemBinding();
	}

	protected void orderNoBinding() {
		if (orderNoInput != null && customerCombo != null)
			orderNoInput.disableIf(isPosted()
				.or(customerCombo.isEmpty()));
	}

	@Override
	protected void orderDateBinding() {
		if (orderDatePicker == null || orderNoInput == null || orderDateDisplay == null)
			return;
		orderDatePicker.disableIf(orderNoInput.isEmpty());
		orderDatePicker.showIf(isNew());
		orderDateDisplay.showIf(orderDatePicker.isNotVisible());
	}

	private void itemBinding() {
		itemCombo.disableIf(orderDatePicker.disabledProperty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerListener();
		orderNoListener();
		orderDateListener();
		itemListener();
		qtyListener();
		remarksListener();
	}

	private void customerListener() {
		if (customerCombo != null && service.isNew())
			customerCombo.onAction(e -> service.setCustomer(customerCombo.getValue()));
	}

	private void orderNoListener() {
		if (orderNoInput != null && service.isNew())
			orderNoInput.onAction(e -> setOrderNoUponValidation(orderNoInput.getValue()));
	}

	private void orderDateListener() {
		if (orderDatePicker != null && service.isNew())
			orderDatePicker.onAction(e -> service.setOrderDate(orderDatePicker.getValue()));
	}

	private void itemListener() {
		if (itemCombo != null && service.isNew())
			itemCombo.onAction(e -> service.setItem(itemCombo.getValue()));
	}

	private void qtyListener() {
		if (qtyInput != null && service.isNew())
			qtyInput.onAction(e -> setQty());
	}

	private void remarksListener() {
		if (remarksDisplay != null && service.isNew())
			remarksDisplay.onAction(e -> service.setRemarks(remarksDisplay.getValue()));
	}

	private void setOrderNoUponValidation(String orderNo) {
		if (service.isNew() && !orderNo.isEmpty())
			try {
				service.setOrderNoUponValidation(orderNo);
			} catch (Exception e) {
				handleError(e, orderNoInput);
			}
	}

	protected void setQty() {
		service.setQty(qtyInput.getValue());
	}

	List<Node> closureNodes() {
		return asList(
			label.name("Closed by"), closedByDisplay.readOnly().width(110).build(TEXT),
			label.name("on"), closedOnDisplay.readOnly().build(TIMESTAMP));
	}

	void qtyInKgDisplayGridNodes(String name,
	                             AppField<BigDecimal> displayField,
	                             int column,
	                             int row,
	                             int labelColumnSpan) {
		qtyDisplayGridNodes(name, displayField, column, row, labelColumnSpan);
		kgLabelGridNode(++column + labelColumnSpan, row);
	}

	private void kgLabelGridNode(int column, int row) {
		labelGridNode("kg", column, row);
	}

	void percentDisplayGridNodes(String name, AppField<BigDecimal> inputField, int column, int row) {
		labelGridNode(name, column, row);
		gridPane.add(inputField.readOnly().width(110).build(PERCENT), ++column, row);
	}

	void percentInputGridNodes(String name, AppField<BigDecimal> inputField, int column, int row) {
		labelGridNode(name, column, row);
		gridPane.add(inputField.width(110).build(PERCENT), ++column, row);
	}

	void qtyInKgInputGridNodes(String name, int column, int row) {
		qtyInKgInputGridNodes(name, qtyInput, column, row);
	}

	void qtyInKgInputGridNodes(String name, AppField<BigDecimal> inputField, int column, int row) {
		labelGridNode(name, column, row);
		qtyInKgInputGridNode(inputField, ++column, row);
	}

	private void qtyInKgInputGridNode(AppField<BigDecimal> inputField, int column, int row) {
		gridPane.add(inputField.width(110).build(QUANTITY), column, row);
		kgLabelGridNode(++column, row);
	}

	void qtyInKgInputGridNode(int column, int row) {
		qtyInKgInputGridNode(qtyInput, column, row);
	}

	void refreshClosureNodes() {
		if (closedByDisplay != null) {
			closedByDisplay.setValue(service.getClosedBy());
			closedOnDisplay.setValue(service.getClosedOn());
		}
	}
}
