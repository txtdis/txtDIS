package ph.txtdis.mgdc.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.Inventory;
import ph.txtdis.mgdc.fx.table.InventoryTable;
import ph.txtdis.service.InventoryService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("inventoryApp")
public class InventoryApp
	extends AbstractExcelApp<InventoryTable, InventoryService, Inventory> {

	@Autowired
	private TotaledTableApp<Inventory> totaledTableApp;

	@Override
	public void refresh() {
		try {
			table.items(service.list());
			totaledTableApp.refresh(service);
			super.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		}
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(2);
		super.start();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(totaledTableApp.addTablePane(table));
	}
}
