package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dto.AgingReceivable;
import ph.txtdis.fx.table.AgingReceivableTable;
import ph.txtdis.service.AgingReceivableService;

@Lazy
@Component("agingReceivableApp")
public class AgingReceivableApp
		extends AbstractExcelApp<AgingReceivableTable, AgingReceivableService, AgingReceivable> {

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
