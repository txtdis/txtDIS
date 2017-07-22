package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Routing;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenuImpl;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.RoutingDialog;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("routingTable")
public class RoutingTable extends AbstractTable<Routing> {

	@Autowired
	private AppendContextMenuImpl<Routing> append;

	@Autowired
	private Column<Routing, String> designatedRoute;

	@Autowired
	private Column<Routing, LocalDate> startDate;

	@Autowired
	private Column<Routing, String> designatedBy;

	@Autowired
	private Column<Routing, ZonedDateTime> designatedOn;

	@Autowired
	private RoutingDialog routingDialog;

	@Override
	protected List<TableColumn<Routing, ?>> addColumns() {
		return asList( //
				designatedRoute.ofType(TEXT).width(180).build("Designated\nRoute", "route"), //
				startDate.ofType(Type.DATE).build("Start\nDate", "startDate"), //
				designatedBy.ofType(TEXT).width(100).build("Designated\nby", "createdBy"), //
				designatedOn.ofType(TIMESTAMP).build("Designated\non", "createdOn"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, routingDialog);
	}
}
