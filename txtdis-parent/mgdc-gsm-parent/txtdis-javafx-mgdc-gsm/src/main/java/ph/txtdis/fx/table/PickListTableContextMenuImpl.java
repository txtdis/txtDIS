package ph.txtdis.fx.table;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import ph.txtdis.dto.Booking;

@Scope("prototype")
@Component("pickListTableContextMenu")
public class PickListTableContextMenuImpl extends AbstractPickListTableContextMenu {

	@Value("${prefix.truck}")
	private String truckPrefix;

	@Override
	protected ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route) {
		bookings.addAll(listWithoutExTruckLoadOrderIfCurrentHas(bookings, route));
		return bookings;
	}

	private List<Booking> listWithoutExTruckLoadOrderIfCurrentHas(List<Booking> bookings, String route) {
		List<Booking> l = service.listUnpickedBookings(route);
		if (bookings.stream().anyMatch(b -> b.getCustomer().startsWith(truckPrefix)))
			l = l.stream().filter(b -> !b.getCustomer().startsWith(truckPrefix)).collect(Collectors.toList());
		return l;
	}
}
