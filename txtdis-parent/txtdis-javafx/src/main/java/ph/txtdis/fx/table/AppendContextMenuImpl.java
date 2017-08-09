package ph.txtdis.fx.table;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.ENTER;
import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import ph.txtdis.app.App;
import ph.txtdis.fx.dialog.InputtedDialog;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.service.AppendableDetailService;

@Scope("prototype")
@Component("appendContextMenu")
public final class AppendContextMenuImpl<S> //
	implements AppendContextMenu<S> {

	private static Logger logger = getLogger(AppendContextMenuImpl.class);

	@Autowired
	private MessageDialog errorDialog;

	private AppTable<S> table;

	private ContextMenu menu;

	private InputtedDialog<S> inputDialog;

	@Override
	public void addItemsToTable(List<S> entities) {
		ObservableList<S> l = observableArrayList(tableItems());
		l.addAll(entities);
		table.setItems(l);
	}

	private List<S> tableItems() {
		List<S> items = table.getItems();
		return items == null ? emptyList() : items.stream().filter(i -> i != null).collect(toList());
	}

	@Override
	public AppendContextMenu<S> addMenu(AppTable<S> t, InputtedDialog<S> d) {
		addMenu(t, d, null);
		return this;
	}

	@Override
	public void addMenu(AppTable<S> t, InputtedDialog<S> d, AppendableDetailService a) {
		table = t;
		inputDialog = d;
		setAppendMenu(a);
	}

	@Override
	public ContextMenu getContextMenu() {
		return menu;
	}

	private void addItemToTable() {
		List<S> s = inputDialog.getAddedItems();
		logger.info("\n    AddedItemsFromDialog@addItemToTable = " + s);
		if (s != null && !s.isEmpty())
			addItemsToTable(s);
	}

	private void append() {
		showAddItemDialog();
		addItemToTable();
		table.scrollTo(table.getItems().size() - 1);
	}

	private void appendIfAppendable(AppendableDetailService a) {
		if (a == null || a.isAppendable())
			append();
		else
			errorDialog.showError(a.getAppendableErrorMessage()).addParent(getStage()).start();
	}

	private MenuItem createAppendMenuItem(AppendableDetailService a) {
		MenuItem item = new MenuItem("Append");
		item.setOnAction(e -> appendIfAppendable(a));
		return item;
	}

	private ContextMenu createTableMenu(AppendableDetailService a) {
		menu = new ContextMenu();
		menu.getItems().addAll(createAppendMenuItem(a));
		return menu;
	}

	private Stage getStage() {
		Scene s = table.getScene();
		return (Stage) s.getWindow();
	}

	private void setAppendMenu(AppendableDetailService a) {
		table.setContextMenu(createTableMenu(a));
		setAppendOnPressedEnterKey(a);
	}

	private void setAppendOnPressedEnterKey(AppendableDetailService a) {
		table.setOnKeyPressed(e -> {
			if (e.getCode() == ENTER && (a == null || a.isAppendable()))
				append();
		});
	}

	private void showAddItemDialog() {
		((App) inputDialog).addParent(getStage()).start();
	}
}
