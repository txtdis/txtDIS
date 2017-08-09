package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.app.SelectableListApp;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.fx.table.BillableListTable;
import ph.txtdis.mgdc.service.TransactionListService;

@Scope("prototype")
@Component("transactionListApp")
public class TransactionListApp
	extends AbstractExcelApp<BillableListTable, TransactionListService, Billable>
	implements SelectableListApp<Billable>,
	LaunchableApp {

	@Override
	public Billable getSelection() {
		return table.getItem();
	}

	@Override
	public void start() {
		setStage(mainVerticalPane());
		table.setItem(null);
		refresh();
		showAndWait();
	}

	@Override
	protected String getTitleText() {
		return getHeaderText();
	}

	@Override
	protected String getHeaderText() {
		return "Transaction List";
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
		service.listInvoicesByTransactedItem(ids);
		refresh();
	}
}
