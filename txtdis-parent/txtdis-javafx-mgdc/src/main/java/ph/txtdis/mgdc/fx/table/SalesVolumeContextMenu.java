package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.mgdc.service.SalesVolumeService;
import ph.txtdis.mgdc.type.SalesVolumeReportType;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.mgdc.type.SalesVolumeReportType.values;

@Scope("prototype")
@Component("salesVolumeContextMenu")
public class SalesVolumeContextMenu {

	@Autowired
	private SalesVolumeService service;

	private SalesVolumeTable table;

	public void addMenu(SalesVolumeTable t) {
		table = t;
		table.setContextMenu(menu());
	}

	private ContextMenu menu() {
		ContextMenu m = new ContextMenu();
		m.getItems().setAll(menuItems());
		return m;
	}

	private List<MenuItem> menuItems() {
		return asList(values()).stream().map(t -> menuItem(t)).collect(toList());
	}

	private MenuItem menuItem(SalesVolumeReportType t) {
		MenuItem i = new MenuItem(t.toString());
		i.setOnAction(e -> setTable(t));
		return i;
	}

	private void setTable(SalesVolumeReportType t) {
		table.setTableColumnVisibility(t);
		table.setId(t.name());
		setTableItems(t);
	}

	private void setTableItems(SalesVolumeReportType t) {
		try {
			service.setType(t);
			table.items(service.list());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
