package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableView;
import ph.txtdis.dyvek.model.BillableDetail;

public interface AssignmentContextMenu {

	void addMenu(TableView<BillableDetail> table);
}