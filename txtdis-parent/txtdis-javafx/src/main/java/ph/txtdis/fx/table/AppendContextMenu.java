package ph.txtdis.fx.table;

import java.util.List;

import javafx.scene.control.ContextMenu;
import ph.txtdis.fx.dialog.InputtedDialog;
import ph.txtdis.service.AppendableDetailService;

public interface AppendContextMenu<S> {

	void addItemsToTable(List<S> entities);

	AppendContextMenu<S> addMenu(AppTable<S> t, InputtedDialog<S> d);

	void addMenu(AppTable<S> t, InputtedDialog<S> d, AppendableDetailService a);

	ContextMenu getContextMenu();
}