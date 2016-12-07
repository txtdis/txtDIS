package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.OTHERS;
import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.Route;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("billableListTable")
public class BillableListTable extends AbstractTableView<Billable> {

	@Autowired
	private Column<Billable, Long> id;

	@Autowired
	private Column<Billable, String> name;

	@Autowired
	private Column<Billable, Route> route;

	@Autowired
	private Column<Billable, String> street;

	@Autowired
	private Column<Billable, Location> barangay;

	@Autowired
	private Column<Billable, Location> city;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				id.ofType(ID).build("ID No.", "id"), //
				name.ofType(Type.TEXT).build("Name", "name"), //
				route.ofType(OTHERS).build("Route", "route"), //
				street.ofType(TEXT).build("Street", "street"), //
				barangay.ofType(OTHERS).width(180).build("Barangay", "barangay"), //
				city.ofType(OTHERS).build("City", "city"));
	}
}