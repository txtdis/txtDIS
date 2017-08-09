package ph.txtdis.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ReportService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractTotaledReportApp<AT extends AppTable<T>, AS extends ReportService<T>, T> //
	extends AbstractReportApp<AT, AS, T> {

	@Autowired
	protected TotaledTableApp<T> totaledTableApp;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(totaledTableApp.addTablePane(table));
	}

	@Override
	public void refresh() {
		table.items(service.list());
		totaledTableApp.refresh(service);
		super.refresh();
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(noOfTotalDisplays());
		super.start();
	}

	protected abstract int noOfTotalDisplays();
}
