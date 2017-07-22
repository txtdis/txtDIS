package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.BookingApp;
import ph.txtdis.dto.Booking;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

public abstract class AbstractPickListTable //
		extends AbstractTable<Booking> //
		implements PickListTable {

	@Autowired
	private Column<Booking, Long> id;

	@Autowired
	private PickListTableContextMenu menu;

	@Autowired
	protected Column<Booking, String> delivery, location, name, route;

	@Autowired
	protected BookingApp app;

	@Override
	public void addMenu() {
		menu.setMenu(this);
	}

	@Override
	protected List<TableColumn<Booking, ?>> addColumns() {
		return asList( //
				id.launches(app).ofType(ID).build("S/O No.", "bookingId"), //
				name.launches(app).ofType(TEXT).build("Name", "customer"), //
				location.launches(app).ofType(TEXT).width(360).build("Location", "location"), //
				route.launches(app).ofType(TEXT).width(180).build("Route", "route"));
	}
}
