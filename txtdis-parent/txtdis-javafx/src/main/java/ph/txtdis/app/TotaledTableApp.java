package ph.txtdis.app;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.TotaledTableService;

public interface TotaledTableApp<T> {

	VBox addNoSubHeadTablePane(AppTable<T> t);

	VBox addTablePane(AppTable<T> t);

	HBox addTotalDisplays(int count);

	void refresh(TotaledTableService<T> s);
}