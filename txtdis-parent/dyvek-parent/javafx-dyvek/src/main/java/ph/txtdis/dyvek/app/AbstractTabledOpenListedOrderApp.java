package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.table.AppTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTabledOpenListedOrderApp<
	OLA extends OpenOrderListApp,
	LA extends OrderListApp,
	AT extends AppTable<BillableDetail>,
	S extends OrderService>
	extends AbstractOpenListedOrderApp<OLA, LA, S> {

	@Autowired
	protected AppFieldImpl<BigDecimal> balanceDisplay, priceInput, totalDisplay;

	@Autowired
	protected AT table;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.add(decisionButton = decisionNeededApp.addDecisionButton());
		return b;
	}

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(1, pane.centeredHorizontal(table.build()));
		return l;
	}

	@Override
	public void refresh() {
		refreshPrice();
		refreshTotals();
		refreshTable();
		refreshBalance();
		super.refresh();
	}

	private void refreshPrice() {
		if (priceInput != null)
			priceInput.setValue(service.getPriceValue());
	}

	protected void refreshTotals() {
		if (totalDisplay != null)
			totalDisplay.setValue(service.getTotalValue());
	}

	private void refreshTable() {
		if (table != null)
			table.items(service.getDetails());
	}

	private void refreshBalance() {
		if (balanceDisplay != null)
			balanceDisplay.setValue(service.getBalanceQty());
	}

	@Override
	protected void secondGridLine() {
		labelGridNode("Item", 0, 1);
		gridPane.add(itemCombo.width(135), 1, 1);
		qtyInKgInputGridNode(2, 1);
		currencyInputGridNodes("Price", priceInput, 110, 4, 1);
		currencyDisplayGridNodes("Total", totalDisplay, 130, 6, 1);
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		saveBinding();
		qtyBinding();
		priceBinding();
		remarksBinding();
		decisionButton.disableIf(isNew());
	}

	protected void saveBinding() {
		if (saveButton != null && priceInput != null)
			saveButton.disableIf(isPosted()
				.or(priceInput.isEmpty()));
	}

	private void qtyBinding() {
		if (qtyInput != null && itemCombo != null)
			qtyInput.disableIf(isPosted()
				.or(itemCombo.isEmpty()));
	}

	private void priceBinding() {
		if (priceInput != null && qtyInput != null)
			priceInput.disableIf(isPosted()
				.or(qtyInput.isEmpty()));
	}

	protected void remarksBinding() {
		if (remarksDisplay != null && priceInput != null)
			remarksDisplay.editableIf(isNew()
				.and(priceInput.isNotEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		setPriceListener();
	}

	private void setPriceListener() {
		if (priceInput != null && service.isNew())
			priceInput.onAction(e -> setPriceAndTotal(priceInput.getValue()));
	}

	private void setPriceAndTotal(BigDecimal price) {
		service.setPriceAndTotalValue(price);
		refreshTotals();
	}

	@Override
	protected void setQty() {
		super.setQty();
		refreshBalance();
	}
}
