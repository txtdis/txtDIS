package ph.txtdis.fx.table;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

@Lazy
@Component("deleteContextMenu")
public final class DeleteContextMenu<S> {

	private TableView<S> table;

	private void addTableMenuItemsToRowMenu(TableView<S> table, ContextMenu rowMenu) {
		if (table != null && table.getContextMenu() != null && rowMenu != null)
			table.getContextMenu().getItems().forEach(tableItem -> {
				MenuItem rowItem = new MenuItem(tableItem.getText());
				rowItem.setGraphic(tableItem.getGraphic());
				rowItem.setOnAction(tableItem.getOnAction());
				rowMenu.getItems().add(rowItem);
			});
	}

	private MenuItem createDeleteRowMenuItem(TableRow<S> row) {
		MenuItem item = new MenuItem("Delete row");
		item.setOnAction(event -> {
			table.getItems().remove(row.getItem());
			table.refresh();
		});
		return item;
	}

	private TableRow<S> createRow(TableView<S> table) {
		TableRow<S> row = new TableRow<>();
		row.contextMenuProperty()
				.bind(Bindings//
						.when(row.itemProperty().isNotNull())//
						.then(createRowMenu(table, row))//
						.otherwise((ContextMenu) null));
		return row;
	}

	private ContextMenu createRowMenu(TableView<S> table, TableRow<S> row) {
		ContextMenu rowMenu = new ContextMenu();
		addTableMenuItemsToRowMenu(table, rowMenu);
		rowMenu.getItems().add(createDeleteRowMenuItem(row));
		return rowMenu;
	}

	protected void addMenu(TableView<S> table) {
		this.table = table;
		table.setRowFactory(t -> createRow(t));
	}
}
