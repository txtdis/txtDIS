package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.app.AccountApp;
import ph.txtdis.mgdc.fx.dialog.RouteDialog;
import ph.txtdis.mgdc.service.RouteService;
import ph.txtdis.type.DeliveryType;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("routeTable")
public class RouteTable
	extends AbstractNameListTable<Route, RouteDialog> {

	@Autowired
	private AccountApp app;

	@Autowired
	private RouteService service;

	@Autowired
	private Column<Route, DeliveryType> type;

	@Autowired
	private Column<Route, String> seller;

	@Autowired
	private Column<Route, String> assignedBy;

	@Autowired
	private Column<Route, ZonedDateTime> assignedOn;

	@Override
	protected List<TableColumn<Route, ?>> addColumns() {
		app.setOnCloseRequest(e -> refreshTable());
		return asList( //
			id.ofType(ID).launches(app).build("ID No.", "id"),
			name.ofType(TEXT).launches(app).width(180).build("Name", "name"),
			type.ofType(ENUM).build("Delivery Type", "type"),
			createdBy.ofType(TEXT).launches(app).width(100).build("Created by", "createdBy"),
			createdOn.ofType(TIMESTAMP).launches(app).build("Created on", "createdOn"),
			seller.ofType(TEXT).launches(app).width(100).build("Current Seller", "seller"),
			assignedBy.ofType(TEXT).launches(app).width(100).build("Assigned by", "assignedBy"),
			assignedOn.ofType(TIMESTAMP).launches(app).build("Assigned\non", "assignedOn"));
	}

	private void refreshTable() {
		try {
			items(service.list());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
