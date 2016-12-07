package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.Listed;
import ph.txtdis.service.Moduled;
import ph.txtdis.service.Titled;

public abstract class AbstractTableApp<AT extends AppTable<T>, AS extends Listed<T>, T> extends AbstractApp {

	@Autowired
	protected AT table;

	@Autowired
	protected AS service;

	@Override
	public void refresh() {
		try {
			table.items(service.list());
			super.refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	public void setFocus() {
		table.requestFocus();
	}

	@Override
	protected String getFontIcon() {
		return ((Moduled) service).getFontIcon();
	}

	@Override
	protected String getHeaderText() {
		return ((Moduled) service).getHeaderText() + " List";
	}

	@Override
	protected String getTitleText() {
		return ((Titled) service).getTitleText();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(box.forHorizontalPane(table.build()));
	}
}
