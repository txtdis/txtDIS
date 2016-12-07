package ph.txtdis.fx.table;

import javafx.scene.control.TableView;
import ph.txtdis.dto.Booking;

public interface PickListTableContextMenu {

	void setMenu(TableView<Booking> table);
}
