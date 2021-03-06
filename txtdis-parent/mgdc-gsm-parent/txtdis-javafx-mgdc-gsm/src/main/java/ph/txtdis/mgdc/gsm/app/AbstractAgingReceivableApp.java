package ph.txtdis.mgdc.gsm.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.AgingReceivable;
import ph.txtdis.mgdc.gsm.fx.table.AgingReceivableTable;
import ph.txtdis.service.AgingReceivableService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractAgingReceivableApp<RS extends AgingReceivableService> //
	extends AbstractExcelApp<AgingReceivableTable, RS, AgingReceivable> //
	implements AgingReceivableApp {

	@Autowired
	private TotaledTableApp<AgingReceivable> totaledTableApp;

	@Override
	public void refresh() {
		try {
			table.items(service.list());
			totaledTableApp.refresh(service);
			super.refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(7);
		super.start();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(totaledTableApp.addTablePane(table));
	}
}
