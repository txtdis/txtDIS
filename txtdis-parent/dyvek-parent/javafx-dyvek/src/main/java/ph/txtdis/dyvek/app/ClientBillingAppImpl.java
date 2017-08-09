package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.table.BillingTable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.service.ClientBillingService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.info.Information;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("clientBillingApp")
public class ClientBillingAppImpl
	extends AbstractTabledOpenListedOrderApp<UnbilledDeliveryListApp, BilledDeliveryListApp, BillingTable,
	ClientBillingService>
	implements ClientBillingApp {

	private AppButton billButton;

	@Autowired
	private AppFieldImpl<String> customerDisplay, itemDisplay, purchaseOrderNoDisplay;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.removeAll(asList(newButton, openOrderButton));
		b.add(0, openOrderButton);
		b.add(billButton = button.icon("clientBill").tooltip("Generate...").build());
		return b;
	}

	@Override
	protected void firstGridLine() {
		textDisplayGridNodes("Customer", customerDisplay, 280, 0, 0, 1);
		textInputGridNodes("Bill No.", orderNoInput, 2, 0);
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 4, 0, 1);
	}

	@Override
	protected void secondGridLine() {
		textDisplayGridNodes("Item", itemDisplay, 280, 0, 1, 1);
		textDisplayGridNodes("P/O No.", purchaseOrderNoDisplay, 110, 2, 1, 1);
		currencyDisplayGridNodes("Total", totalDisplay, 110, 4, 1);
	}

	@Override
	protected void thirdGridLine() {
		remarksGridNodes(2, 5);
	}

	@Override
	public void goToDefaultFocus() {
		if (service.isNew() && service.getCustomer() != null)
			orderNoInput.requestFocus();
		else
			openOrderButton.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(
			gridPane(),
			pane.centeredHorizontal(table.build()),
			trackedPane());
	}

	@Override
	@Lookup("billedDeliveryListApp")
	protected BilledDeliveryListApp orderListApp() {
		return null;
	}

	@Override
	protected void orderNoBinding() {
		orderNoInput.disableIf(isPosted());
	}

	@Override
	public void refresh() {
		customerDisplay.setValue(service.getCustomer());
		itemDisplay.setValue(service.getItem());
		purchaseOrderNoDisplay.setValue(service.getSalesNo());
		super.refresh();
	}

	@Override
	protected void refreshDateDisplay() {
		orderDateDisplay.setValue(service.getBillDate());
	}

	@Override
	protected void refreshDatePicker() {
		orderDatePicker.setValue(service.getOrderDate());
	}

	@Override
	protected void refreshOrderNo() {
		orderNoInput.setValue(service.getOrderNo());
	}

	@Override
	protected void post() throws Information, Exception {
		ensureOrderNoAndDateExist();
		super.post();
	}

	private void ensureOrderNoAndDateExist() throws Exception {
		if (service.getOrderNo() == null)
			service.setOrderNoUponValidation(orderNoInput.getValue());
		if (service.getOrderDate() == null)
			service.setOrderDate(orderDatePicker.getValue());
	}

	@Override
	protected void saveBinding() {
		saveButton.disableIf(isPosted()
			.or(orderDatePicker.isEmpty()));
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		billButton.disableIf(isNew());
		table.readOnly();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		billButton.onAction(e -> generateBill());
		table.setOnItemChange(e -> updateTotals(table.getItems()));
	}

	private void generateBill() {
		try {
			service.generateBill();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void updateTotals(List<BillableDetail> items) {
		service.updateTotals(items);
		refreshTotals();
	}

	@Override
	@Lookup("unbilledDeliveryListApp")
	protected UnbilledDeliveryListApp openOrderListApp() {
		return null;
	}

	@Override
	protected void actOnSelection(String id) {
		actOn(id.toString());
	}
}
