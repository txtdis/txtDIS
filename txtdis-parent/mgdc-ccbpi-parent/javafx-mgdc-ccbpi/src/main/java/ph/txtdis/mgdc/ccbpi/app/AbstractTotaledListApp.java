package ph.txtdis.mgdc.ccbpi.app;

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.ccbpi.service.ListService;

public abstract class AbstractTotaledListApp<AT extends AppTable<SalesItemVariance>, AS extends ListService> //
		extends AbstractExcelApp<AT, AS, SalesItemVariance> //
		implements LaunchableApp {

	private static Logger logger = getLogger(AbstractTotaledListApp.class);

	@Autowired
	private TotaledTableApp<SalesItemVariance> totaledTableApp;

	@Override
	public void refresh() {
		try {
			super.refresh();
			totaledTableApp.refresh(service);
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(2);
		super.start();
	}

	@Override
	public void actOn(String... id) {
		try {
			open(id);
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void open(String[] ids) throws Exception {
		logger.info("\n    ids = " + ids);
		setList(ids);
		refresh();
	}

	protected abstract void setList(String[] ids) throws Exception;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(totaledTableApp.addTablePane(table));
	}
}
