package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.app.BookingApp;
import ph.txtdis.dto.Booking;

public abstract class AbstractPickListTable extends AbstractTableView<Booking> implements PickListTable {

	@Autowired
	private Column<Booking, Long> id;

	@Autowired
	private PickListTableContextMenu menu;

	@Autowired
	protected Column<Booking, String> location, name, route;

	@Autowired
	protected BookingApp app;

	@Override
	public void addMenu() {
		menu.setMenu(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll( //
				id.launches(app).ofType(ID).build("S/O No.", "bookingId"), //
				name.launches(app).ofType(TEXT).build("Name", "customer"), //
				location.launches(app).ofType(TEXT).width(360).build("Location", "location"), //
				route.launches(app).ofType(TEXT).width(180).build("Route", "route"));
	}
}
