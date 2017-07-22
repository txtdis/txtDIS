package ph.txtdis.dyvek.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import ph.txtdis.dyvek.fx.table.AssignmentTable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.service.BillingService;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;

public abstract class AbstractBillingApp< //
		OLA extends OpenOrderListApp, //
		LA extends OrderListApp, //
		S extends BillingService> //
		extends AbstractTabledOpenListedOrderApp<OLA, LA, AssignmentTable, S> {

	@Autowired
	protected AppButtonImpl billActionButton;

	@Autowired
	protected AppFieldImpl<BigDecimal> adjustmentQtyDisplay, adjustmentPriceDisplay, adjustmentValueDisplay, netDisplay;

	@Autowired
	protected AppFieldImpl<String> vendorDisplay, recipientDisplay, itemDisplay, billActedByDisplay;

	@Autowired
	protected AppFieldImpl<ZonedDateTime> billActedOnDisplay;

	private BooleanProperty assignedAndDeliverQtyDiffer;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = super.addButtons();
		b.removeAll(asList(newButton, openOrderButton));
		b.add(0, openOrderButton);
		return b;
	}

	@Override
	protected List<Node> creationNodes() {
		List<Node> l = new ArrayList<>(super.creationNodes());
		l.addAll(logNodes(billActedByPrompt(), billActedByDisplay, billActedOnDisplay));
		return l;
	}

	protected abstract String billActedByPrompt();

	@Override
	protected void buttonListeners() {
		super.buttonListeners();
		billActionButton.onAction(e -> openBillingDialog());
	}

	protected abstract void openBillingDialog();

	@Override
	protected void firstGridLine() {
		textDisplayGridNodes("Supplied by", vendorDisplay, 280, 0, 0, 4);
		textDisplayGridNodes("D/R No.", orderNoInput, 110, 5, 0, 1);
		dateDisplayGridNodes("D/R Date", orderDateDisplay, 7, 0);
	}

	@Override
	protected void secondGridLine() {
		textDisplayGridNodes("Delivered to", recipientDisplay, 280, 0, 1, 4);
		textDisplayGridNodes("Item", itemDisplay, 110, 5, 1, 1);
		qtyDisplayGridNodes("Quantity", qtyInput, 7, 1);
	}

	@Override
	protected void thirdGridLine() {
		gridLine3Billing();
		remarksGridNodes(3, 8);
	}

	protected void gridLine3Billing() {
		labelGridNode("Adjustment", 0, 2);
		qtyDisplayGridNodes("Quantity", adjustmentQtyDisplay, 1, 2);
		currencyDisplayGridNodes("Price", adjustmentPriceDisplay, 110, 3, 2);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList( //
				gridPane(), //
				box.forHorizontalPane(table.build()), //
				box.forHorizontalPane(totalNodes()), //
				trackedPane());
	}

	private List<Node> totalNodes() {
		return asList(//
				label.name("Gross"), totalDisplay.readOnly().build(CURRENCY), //
				label.name("Adjustment"), adjustmentValueDisplay.readOnly().build(CURRENCY), //
				label.name("Net"), netDisplay.readOnly().build(CURRENCY));
	}

	@Override
	protected void orderDateBinding() {
	}

	@Override
	public void refresh() {
		super.refresh();
		vendorDisplay.setValue(service.getCustomer());
		recipientDisplay.setValue(service.getRecipient());
		itemDisplay.setValue(service.getItem());
		adjustmentQtyDisplay.setValue(service.getAdjustmentQty());
		adjustmentPriceDisplay.setValue(service.getAdjustmentPrice());
		openOrderButton.requestFocus();
	}

	@Override
	protected void refreshLogNodes() {
		super.refreshLogNodes();
		billActedByDisplay.setValue(service.getBillActedByBy());
		billActedOnDisplay.setValue(service.getBillActedOn());
	}

	@Override
	protected void refreshTotals() {
		super.refreshTotals();
		adjustmentValueDisplay.setValue(service.getAdjustmentValue());
		netDisplay.setValue(service.getNetValue());
		assignedAndDeliverQtyDiffer.set(service.areAssignedAndDeliverQtyDifferent());
	}

	@Override
	protected void remarksBinding() {
		if (remarksDisplay != null)
			remarksDisplay.editableIf(isNew());
	}

	@Override
	protected void save() {
		service.setDetails(table.getItems());
		super.save();
	}

	@Override
	protected void saveBinding() {
		assignedAndDeliverQtyDiffer = new SimpleBooleanProperty(service.areAssignedAndDeliverQtyDifferent());
		if (saveButton != null)
			saveButton.disableIf(isPosted() //
					.or(assignedAndDeliverQtyDiffer));
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		billActionButtonBinding();
		table.editableIf(isNew());
	}

	protected void billActionButtonBinding() {
		billActionButton.disableIf(isNew() //
				.or(billActedOnDisplay.isNotEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		openOrderButton.onAction(e -> startOpenOrderListApp());
		table.setOnItemChange(e -> updateTotals(table.getItems()));
	}

	private void updateTotals(List<BillableDetail> items) {
		service.updateTotals(items);
		refreshTotals();
	}
}
