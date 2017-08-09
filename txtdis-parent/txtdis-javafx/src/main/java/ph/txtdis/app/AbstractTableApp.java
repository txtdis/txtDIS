package ph.txtdis.app;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractTableApp<AT extends AppTable<T>, AS extends ListedAndResettableService<T>, T> //
	extends AbstractApp<AS> {

	@Autowired
	protected AT table;

	@Override
	protected void clear() {
		super.clear();
		if (table != null)
			table.removeListener();
	}

	@Override
	public void refresh() {
		refreshTable();
		super.refresh();
	}

	protected void refreshTable() {
		if (table != null)
			table.items(service.list());
	}

	@Override
	public void goToDefaultFocus() {
		table.requestFocus();
	}

	@Override
	protected String getFontIcon() {
		return ((TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService) service).getIconName();
	}

	@Override
	protected String getHeaderText() {
		return ((TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService) service).getHeaderName() + " List";
	}

	@Override
	protected String getTitleText() {
		return ((TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService) service).getTitleName();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(tablePane());
	}

	protected HBox tablePane() {
		return pane.centeredHorizontal(table.build());
	}
}
