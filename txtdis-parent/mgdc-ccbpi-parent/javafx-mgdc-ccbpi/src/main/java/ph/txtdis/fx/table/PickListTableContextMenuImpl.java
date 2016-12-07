package ph.txtdis.fx.table;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import ph.txtdis.dto.Booking;

@Lazy
@Component("pickListTableContextMenu")
public class PickListTableContextMenuImpl extends AbstractPickListTableContextMenu {

	@Override
	protected ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route) {
		bookings.addAll(service.listUnpickedBookings(route));
		return bookings;
	}
}
