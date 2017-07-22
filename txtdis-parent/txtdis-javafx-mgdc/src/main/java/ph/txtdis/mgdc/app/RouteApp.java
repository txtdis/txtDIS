package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.fx.table.RouteTable;
import ph.txtdis.mgdc.service.RouteService;

@Scope("prototype")
@Component("routeApp")
public class RouteApp extends AbstractTableApp<RouteTable, RouteService, Route> {
}
