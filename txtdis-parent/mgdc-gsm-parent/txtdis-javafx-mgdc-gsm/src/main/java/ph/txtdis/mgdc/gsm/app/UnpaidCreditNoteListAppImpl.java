package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractListApp;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.mgdc.fx.table.CreditNoteListTable;
import ph.txtdis.mgdc.service.CreditNoteService;

@Scope("prototype")
@Component("unpaidCreditNoteListApp")
public class UnpaidCreditNoteListAppImpl //
		extends AbstractListApp<CreditNoteListTable, CreditNoteService, CreditNote> //
		implements UnpaidCreditNoteListApp {

	@Override
	protected String getHeaderText() {
		return service.getUnpaidHeaderText();
	}

	@Override
	public void refresh() {
		table.items(service.listUnvalidated());
		refreshTitleAndHeader();
		goToDefaultFocus();
	}

	@Override
	protected void saveAsExcel(CreditNoteListTable table) throws Exception {
		service.writeUnpaidExcelFile(table);
	}
}
