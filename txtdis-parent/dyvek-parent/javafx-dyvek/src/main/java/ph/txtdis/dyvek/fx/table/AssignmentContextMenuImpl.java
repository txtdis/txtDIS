package ph.txtdis.dyvek.fx.table;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.AssignmentDialog;
import ph.txtdis.dyvek.model.BillableDetail;

import static javafx.beans.binding.Bindings.when;
import static javafx.collections.FXCollections.observableArrayList;
import static ph.txtdis.util.NumberUtils.isPositive;

@Scope("prototype")
@Component("assignmentContextMenu")
public class AssignmentContextMenuImpl //
	implements AssignmentContextMenu {

	private TableView<BillableDetail> table;

	private AssignmentDialog dialog;

	@Override
	public void addMenu(TableView<BillableDetail> table, AssignmentDialog dialog) {
		this.table = table;
		this.dialog = dialog;
		table.setRowFactory(t -> createRowMenu(t));
	}

	private TableRow<BillableDetail> createRowMenu(TableView<BillableDetail> table) {
		TableRow<BillableDetail> row = new TableRow<>();
		row.contextMenuProperty().bind(when( //
			row.itemProperty().isNotNull() //
				.and(table.editableProperty())) //
			.then(createRowMenu(table, row))//
			.otherwise((ContextMenu) null));
		return row;
	}

	private ContextMenu createRowMenu(TableView<BillableDetail> table, TableRow<BillableDetail> row) {
		ContextMenu menu = new ContextMenu();
		addTableMenuItemsToRowMenu(table, menu);
		menu.getItems().add(createQtyAssignmentMenuItem(row));
		return menu;
	}

	private void addTableMenuItemsToRowMenu(TableView<BillableDetail> table, ContextMenu menu) {
		if (table != null && table.getContextMenu() != null && menu != null)
			table.getContextMenu().getItems().forEach(item -> addRowMenuItem(menu, item));
	}

	private MenuItem createQtyAssignmentMenuItem(TableRow<BillableDetail> row) {
		MenuItem item = new MenuItem("Assign");
		item.setOnAction(e -> assignQtyToSelection(row.getItem()));
		return item;
	}

	private void addRowMenuItem(ContextMenu menu, MenuItem tableMenuItem) {
		MenuItem rowItem = new MenuItem(tableMenuItem.getText());
		rowItem.setGraphic(tableMenuItem.getGraphic());
		rowItem.setOnAction(tableMenuItem.getOnAction());
		menu.getItems().add(rowItem);
	}

	private void assignQtyToSelection(BillableDetail unassigned) {
		dialog.setDetail(unassigned).addParent(getStage()).start();
		BillableDetail assigned = dialog.getDetail();
		if (assigned != null && isPositive(assigned.getAssignedQty()))
			assignQtyToSelection(unassigned, assigned);
	}

	private Stage getStage() {
		Scene s = table.getScene();
		return (Stage) s.getWindow();
	}

	private void assignQtyToSelection(BillableDetail unassigned, BillableDetail assigned) {
		ObservableList<BillableDetail> l = observableArrayList(table.getItems());
		l.set(l.indexOf(unassigned), assigned);
		table.setItems(l);
		table.refresh();
	}
}
