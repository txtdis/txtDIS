package ph.txtdis.fx.table;

import static javafx.collections.FXCollections.observableArrayList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.app.MultiTyped;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Typed;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("tabularCell")
public class TabularCell<S extends Keyed<?>, T> {

	private AbstractTable<S> table;

	private Boolean isDoubleClickEnabled;

	private LaunchableApp app;

	private String[] selectionIds;

	private S tableItem;

	private Stage stage;

	private TableCell<S, T> tableCell;

	private TableColumn<S, T> tableColumn;

	public TableCell<S, T> get(LaunchableApp app, Type type) {
		this.app = app;
		isDoubleClickEnabled = app != null;
		return textFieldCell(type);
	}

	private TableCell<S, T> textFieldCell(Type type) {
		FieldCell<S, T> cell = new FieldCell<>(type);
		if (isDoubleClickEnabled != false)
			cell.setOnMouseClick(e -> onMouseClick(e));
		return cell;
	}

	@SuppressWarnings("unchecked")
	private void onMouseClick(MouseEvent e) {
		if (e.getClickCount() < 2)
			return;
		tableCell = (TableCell<S, T>) e.getSource();
		if (itemExists())
			onDoubleMouseClicks();
	}

	@SuppressWarnings("unchecked")
	private boolean itemExists() {
		TableRow<S> row = tableCell.getTableRow();
		tableItem = row.getItem();
		return tableItem != null;
	}

	private void onDoubleMouseClicks() {
		tableColumn = tableCell.getTableColumn();
		table = (AbstractTable<S>) tableColumn.getTableView();
		selectionIds = new String[]{itemId(), columnIdx(), appData()};
		stage = (Stage) tableCell.getScene().getWindow();
		launchAppIfAble();
	}

	private String itemId() {
		return tableItem.getId().toString();
	}

	private String columnIdx() {
		List<TableColumn<S, ?>> columns = table.getColumns();
		return String.valueOf(columns.indexOf(tableColumn));
	}

	private String appData() {
		return (String) tableColumn.getUserData();
	}

	private void launchAppIfAble() {
		if (app != null && !app.isDialogCloserOnly())
			launch();
		else if (isDoubleClickEnabled == true)
			setSelectedItem();
	}

	private void launch() {
		app.addParent(stage);
		if (app instanceof AbstractFieldDialog<?>)
			launchDialog();
		else
			launchApp();
	}

	private void setSelectedItem() {
		table.setItem(tableItem);
		stage.close();
	}

	private void launchDialog() {
		app.actOn(selectionIds);
		app.start();
		refreshTable();
	}

	private void launchApp() {
		if (app instanceof MultiTyped)
			((MultiTyped) app).type(type());
		app.start();
		app.actOn(selectionIds);
	}

	private void refreshTable() {
		ObservableList<S> l = observableArrayList(table.getItems());
		l.set(Integer.valueOf(selectionIds[0]) - 1, addedItem());
		table.setItems(l);
	}

	private ModuleType type() {
		return ((Typed) tableItem).type();
	}

	@SuppressWarnings("unchecked")
	private S addedItem() {
		List<S> l = ((AbstractFieldDialog<S>) app).getAddedItems();
		return l == null || l.isEmpty() ? null : l.get(0);
	}

	@SuppressWarnings("unchecked")
	public TableCell<S, T> get(String field) {
		isDoubleClickEnabled = false;
		return (TableCell<S, T>) new CheckboxCell<S>(field);
	}
}
