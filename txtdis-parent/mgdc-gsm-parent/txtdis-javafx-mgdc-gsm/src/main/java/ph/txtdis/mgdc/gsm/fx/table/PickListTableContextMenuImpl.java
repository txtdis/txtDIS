package ph.txtdis.mgdc.gsm.fx.table;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Booking;
import ph.txtdis.mgdc.fx.table.AbstractPickListTableContextMenu;

import java.util.List;

@Scope("prototype")
@Component("pickListTableContextMenu")
public class PickListTableContextMenuImpl //
	extends AbstractPickListTableContextMenu {

	@Value("${prefix.truck}")
	private String truckPrefix;

	@Override
	protected ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route) {
		bookings.addAll(listWithoutExTruckLoadOrderIfCurrentHas(bookings, route));
		return bookings;
	}

	private List<Booking> listWithoutExTruckLoadOrderIfCurrentHas(List<Booking> bookings, String route) {
		List<Booking> l = service.listUnpickedBookings(route);
		//		if (bookings.stream().anyMatch(b -> b.getCustomer().startsWith(truckPrefix)))
		//			l = l.stream().filter(b -> !b.getCustomer().startsWith(truckPrefix)).collect(Collectors.toList());
		return l;
	}
}
