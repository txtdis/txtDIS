package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ReportService;

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
