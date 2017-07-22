package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.OTHERS;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("customerListTable")
public class CustomerListTableImpl //
		extends AbstractTable<Customer> //
		implements CustomerListTable {

	@Autowired
	private DialogClosingOnlyApp app;

	@Autowired
	private Column<Customer, Long> vendorId;

	@Autowired
	private Column<Customer, String> name;

	@Autowired
	private Column<Customer, Channel> channel;

	@Autowired
	private Column<Customer, Route> route;

	@Override
	protected List<TableColumn<Customer, ?>> addColumns() {
		return asList( //
				vendorId.ofType(ID).launches(app).build("ID No.", "vendorId"), //
				name.ofType(Type.TEXT).launches(app).build("Name", "name"), //
				channel.ofType(OTHERS).launches(app).width(60).build("Route", "channel"), //
				route.ofType(OTHERS).launches(app).width(80).build("Delivery", "route"));
	}
}
