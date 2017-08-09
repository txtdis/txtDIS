package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("customerListTable")
public class CustomerListTableImpl //
	extends AbstractTable<Customer> //
	implements CustomerListTable {

	@Autowired
	private Column<Customer, Long> id;

	@Autowired
	private Column<Customer, Boolean> active;

	@Autowired
	private Column<Customer, String> name, visit, street;

	@Autowired
	private Column<Customer, Route> route;

	@Autowired
	private Column<Customer, Location> barangay, city;

	@Autowired
	private DialogClosingOnlyApp app;

	@Override
	protected List<TableColumn<Customer, ?>> addColumns() {
		return asList( //
			id.ofType(ID).launches(app).build("ID No.", "id"), //
			name.ofType(TEXT).launches(app).width(240).build("Name", "name"), //
			active.ofType(BOOLEAN).width(80).build("Active", "active"), //
			visit.ofType(TEXT).launches(app).width(60).build("Visit", "visitDay"), //
			route.ofType(OTHERS).launches(app).build("Route", "route"), //
			street.ofType(TEXT).launches(app).width(300).build("Street", "street"), //
			barangay.ofType(OTHERS).launches(app).width(180).build("Barangay", "barangay"), //
			city.ofType(OTHERS).launches(app).width(160).build("City", "city"));
	}
}
