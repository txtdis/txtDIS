package ph.txtdis.mgdc.gsm.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import ph.txtdis.app.AbstractRemarkedKeyedApp;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.dto.StockTake;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.StockTakeTable;
import ph.txtdis.mgdc.gsm.service.StockTakeService;

public abstract class AbstractStockTakeApp //
		extends AbstractRemarkedKeyedApp<StockTakeService, StockTake, Long, Long> //
		implements StockTakeApp {

	@Autowired
	private AppCombo<String> warehouseCombo, checkerCombo, takerCombo;

	@Autowired
	private StockTakeTable table;

	private BooleanProperty isCountToday, isUserAnAuditor;

	@Override
	public void actOn(String... id) {
		try {
			service.openByDate(id[2]);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), box.forHorizontalPane(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateStackPane(), 1, 0, 2, 1);
		gridPane.add(label.field("Warehouse"), 3, 0);
		gridPane.add(warehouseCombo.items(service.listWarehouses()), 4, 0);
		gridPane.add(label.field("Checker"), 5, 0);
		gridPane.add(checkerCombo.items(service.listCheckers()), 6, 0);
		gridPane.add(label.field("Taker"), 7, 0);
		gridPane.add(takerCombo.items(service.listTakers()), 8, 0);
		gridPane.add(label.field("Remarks"), 0, 1);
		gridPane.add(remarksDisplay(), 1, 1, 10, 2);
		return gridPane;
	}

	private ScrollPane remarksDisplay() {
		remarksDisplay.build();
		remarksDisplay.makeEditable();
		return remarksDisplay.get();
	}

	@Override
	public void refresh() {
		orderDateDisplay.setValue(service.getCountDate());
		warehouseCombo.items(service.listWarehouses());
		checkerCombo.items(service.listCheckers());
		takerCombo.items(service.listTakers());
		table.items(service.getDetails());
		isCountToday.set(service.isCountToday());
		super.refresh();
	}

	@Override
	public void goToDefaultFocus() {
		warehouseCombo.requestFocus();
	}

	@Override
	protected Node orderDateStackPane() {
		orderDateDisplay.readOnly().build(DATE);
		return super.orderDateStackPane();
	}

	@Override
	protected void setBindings() {
		isCountToday = new SimpleBooleanProperty(service.isCountToday());
		isUserAnAuditor = new SimpleBooleanProperty(service.isUserAStockTaker());
		saveButton.disableIf(table.isEmpty()//
				.or(isCountToday.not())//
				.or(isPosted())//
				.or(isUserAnAuditor.not()));
		checkerCombo.disableIf(warehouseCombo.isEmpty());
		takerCombo.disableIf(checkerCombo.isEmpty());
		table.disableIf(takerCombo.isCurrentlyDisabled());
		remarksDisplay.editableIf(isPosted().not());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		warehouseCombo.onAction(e -> setWarehouseIfAllCountDateTransactionsAreComplete());
		checkerCombo.onAction(e -> service.setChecker(checkerCombo.getValue()));
		takerCombo.onAction(e -> service.setTaker(takerCombo.getValue()));
		table.setOnItemChange(e -> service.setDetails(table.getItems()));
	}

	private void setWarehouseIfAllCountDateTransactionsAreComplete() {
		if (service.isNew())
			try {
				service.setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(warehouseCombo.getValue());
			} catch (Exception e) {
				showErrorDialog(e);
				refresh();
			}
	}
}
