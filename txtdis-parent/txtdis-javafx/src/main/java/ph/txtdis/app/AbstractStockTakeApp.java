package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import ph.txtdis.dto.StockTake;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.StockTakeTable;
import ph.txtdis.service.StockTakeService;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractStockTakeApp extends AbstractIdApp<StockTakeService, StockTake, Long, Long>
		implements StockTakeApp {

	@Autowired
	private AppCombo<String> warehouseCombo, checkerCombo, takerCombo;

	@Autowired
	private AppField<LocalDate> countDateDisplay;

	@Autowired
	private AppGridPane gridPane;

	@Autowired
	private LabelFactory label;

	@Autowired
	private StockTakeTable table;

	private BooleanProperty isCountToday, isOffSite, isUserAnAuditor;

	@Override
	public void actOn(String... id) {
		try {
			LocalDate date = DateTimeUtils.toDate(id[2]);
			service.open(date);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	public void refresh() {
		countDateDisplay.setValue(service.getCountDate());
		warehouseCombo.items(service.listWarehouses());
		checkerCombo.items(service.listCheckers());
		takerCombo.items(service.listTakers());
		table.items(service.getDetails());
		isCountToday.set(service.isCountToday());
		super.refresh();
	}

	@Override
	public void setFocus() {
		warehouseCombo.requestFocus();
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(countDateDisplay.readOnly().build(DATE), 1, 0, 2, 1);
		gridPane.add(label.field("Warehouse"), 3, 0);
		gridPane.add(warehouseCombo.items(service.listWarehouses()), 4, 0);
		gridPane.add(label.field("Checker"), 5, 0);
		gridPane.add(checkerCombo.items(service.listCheckers()), 6, 0);
		gridPane.add(label.field("Taker"), 7, 0);
		gridPane.add(takerCombo.items(service.listTakers()), 8, 0);
		gridPane.add(label.field("Remarks"), 0, 1);
		gridPane.add(remarksDisplay.build(), 1, 1, 10, 2);
		return gridPane;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), box.forHorizontalPane(table.build()), trackedPane());
	}

	@Override
	protected void setBindings() {
		isCountToday = new SimpleBooleanProperty(service.isCountToday());
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
		isUserAnAuditor = new SimpleBooleanProperty(service.isUserAStockTaker());
		saveButton.disableIf(table.isEmpty()//
				.or(isCountToday.not())//
				.or(isOffSite)//
				.or(isPosted())//
				.or(isUserAnAuditor.not()));
		checkerCombo.disableIf(warehouseCombo.isEmpty());
		takerCombo.disableIf(checkerCombo.isEmpty());
		table.disableIf(takerCombo.isEmpty());
		remarksDisplay.editableIf(isPosted().not());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		warehouseCombo.setOnAction(e -> setWarehouseIfAllCountDateTransactionsAreComplete());
		checkerCombo.setOnAction(e -> service.setChecker(checkerCombo.getValue()));
		takerCombo.setOnAction(e -> service.setTaker(takerCombo.getValue()));
		table.setOnItemChange(e -> service.setDetails(table.getItems()));
	}

	private void setWarehouseIfAllCountDateTransactionsAreComplete() {
		if (service.isNew())
			try {
				service.setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(
						warehouseCombo.getValue());
			} catch (Exception e) {
				showErrorDialog(e);
				refresh();
			}
	}
}
