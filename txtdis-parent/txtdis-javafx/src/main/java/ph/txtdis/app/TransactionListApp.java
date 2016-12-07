package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.fx.table.BillableListTable;
import ph.txtdis.service.TransactionListService;

@Scope("prototype")
@Component("transactionListApp")
public class TransactionListApp extends AbstractExcelApp<BillableListTable, TransactionListService, Billable>
		implements AppSelectable<Billable>, Launchable {

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
	protected String getHeaderText() {
		return "Transaction List";
	}

	@Override
	protected String getTitleText() {
		return getHeaderText();
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
