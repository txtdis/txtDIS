package ph.txtdis.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.fx.table.CustomerReceivableTable;
import ph.txtdis.service.CustomerReceivableService;

@Scope("prototype")
@Component("customerReceivableApp")
public class CustomerReceivableAppImpl //
		extends AbstractExcelApp<CustomerReceivableTable, CustomerReceivableService, CustomerReceivable> // 
		implements CustomerReceivableApp {

	@Autowired
	private TotaledTableApp<CustomerReceivable> totaledTableApp;

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
		service.listInvoicesByCustomerBetweenTwoDayCounts(ids);
		refresh();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(totaledTableApp.addTablePane(table));
	}
}
