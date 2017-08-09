package ph.txtdis.mgdc.ccbpi.fx.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ph.txtdis.dto.Booking;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.mgdc.ccbpi.service.CokePickListService;
import ph.txtdis.mgdc.fx.table.AbstractPickListTableContextMenu;

@Scope("prototype")
@Component("pickListTableContextMenu")
public class CokePickListTableContextMenuImpl //
	extends AbstractPickListTableContextMenu {

	@Autowired
	private MessageDialog errorDialog;

	@Autowired
	private CokePickListService cokePickListService;

	@Override
	protected void append(String route) {
		if (cokePickListService.isAppendable())
			super.append(route);
		else
			errorDialog.showError("One delivery route per picklist").addParent(getStage()).start();
	}

	private Stage getStage() {
		Scene s = table.getScene();
		return (Stage) s.getWindow();
	}

	@Override
	protected ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route) {
		bookings.addAll(service.listUnpickedBookings(route));
		return bookings;
	}
}
