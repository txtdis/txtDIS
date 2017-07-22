package ph.txtdis.mgdc.ccbpi.fx.table;

import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Booking;
import ph.txtdis.mgdc.fx.table.AbstractPickListTable;

@Scope("prototype")
@Component("pickListTable")
public class PickListTableImpl extends AbstractPickListTable {

	@Override
	protected List<TableColumn<Booking, ?>> addColumns() {
		return Arrays.asList(//
				location.launches(app).ofType(TEXT).width(180).build("Order No.", "location"), //
				name.launches(app).ofType(TEXT).width(240).build("Outlet", "customer"), //
				route.launches(app).ofType(TEXT).width(60).build("Route", "route"), //
				delivery.launches(app).ofType(TEXT).width(100).build("Delivery", "deliveryRoute")); //
	}
}
