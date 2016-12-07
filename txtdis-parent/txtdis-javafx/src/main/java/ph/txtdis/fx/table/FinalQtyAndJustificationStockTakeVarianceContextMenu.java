package ph.txtdis.fx.table;

import static javafx.beans.binding.Bindings.when;
import static javafx.collections.FXCollections.observableArrayList;
import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.fx.dialog.FinalQtyAndJustificationInputDialog;

@Scope("prototype")
@Component("finalQtyAndJustificationStockTakeVarianceContextMenu")
public class FinalQtyAndJustificationStockTakeVarianceContextMenu {

	private static Logger logger = getLogger(FinalQtyAndJustificationStockTakeVarianceContextMenu.class);

	@Autowired
	private FinalQtyAndJustificationInputDialog dialog;

	private TableView<StockTakeVariance> table;

	public void addMenu(TableView<StockTakeVariance> table) {
		this.table = table;
		table.setRowFactory(t -> createRowMenu(t));
	}

	private void addRowMenuItem(ContextMenu cm, MenuItem tableItem) {
		MenuItem rowItem = new MenuItem(tableItem.getText());
		rowItem.setGraphic(tableItem.getGraphic());
		rowItem.setOnAction(tableItem.getOnAction());
		cm.getItems().add(rowItem);
	}

	private void addTableMenuItemsToRowMenu(TableView<StockTakeVariance> t, ContextMenu cm) {
		if (t != null && t.getContextMenu() != null && cm != null)
			t.getContextMenu().getItems().forEach(i -> addRowMenuItem(cm, i));
	}

	private MenuItem createInputFinalQtyAndJusticationRowMenuItem(TableRow<StockTakeVariance> row) {
		MenuItem mi = new MenuItem("Adjust");
		mi.setOnAction(e -> inputFinalQtyAndJustication(row));
		return mi;
	}

	private TableRow<StockTakeVariance> createRowMenu(TableView<StockTakeVariance> t) {
		TableRow<StockTakeVariance> r = new TableRow<>();
		r.contextMenuProperty()
				.bind(when(r.itemProperty().isNotNull()) //
						.then(createRowMenu(t, r)) //
						.otherwise((ContextMenu) null));
		return r;
	}

	private ContextMenu createRowMenu(TableView<StockTakeVariance> t, TableRow<StockTakeVariance> r) {
		ContextMenu cm = new ContextMenu();
		addTableMenuItemsToRowMenu(t, cm);
		cm.getItems().add(createInputFinalQtyAndJusticationRowMenuItem(r));
		return cm;
	}

	private void inputFinalQtyAndJustication(TableRow<StockTakeVariance> r) {
		ObservableList<StockTakeVariance> l = observableArrayList(table.getItems());
		int index = index(l, r);
		StockTakeVariance variance = getEditedVarianceFromInputFinalQtyAndJusticationDialog(r);
		logger.info("\n    VarianceToReplace@" + index + ": " + variance);
		l.set(index, variance);
		table.setItems(l);
		table.refresh();
	}

	private int index(ObservableList<StockTakeVariance> l, TableRow<StockTakeVariance> r) {
		return l.indexOf(r.getItem());
	}

	private StockTakeVariance getEditedVarianceFromInputFinalQtyAndJusticationDialog(TableRow<StockTakeVariance> r) {
		dialog.varianceToEdit(r.getItem()).addParent(getStage()).start();
		return dialog.getVariance();
	}

	private Stage getStage() {
		Scene s = table.getScene();
		return (Stage) s.getWindow();
	}
}
