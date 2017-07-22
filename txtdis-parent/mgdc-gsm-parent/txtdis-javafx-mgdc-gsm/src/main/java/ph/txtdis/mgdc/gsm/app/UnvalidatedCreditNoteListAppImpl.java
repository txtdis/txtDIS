package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractListApp;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.mgdc.fx.table.CreditNoteListTable;
import ph.txtdis.mgdc.service.CreditNoteService;

@Scope("prototype")
@Component("unvalidatedCreditNoteListApp")
public class UnvalidatedCreditNoteListAppImpl //
		extends AbstractListApp<CreditNoteListTable, CreditNoteService, CreditNote> //
		implements UnvalidatedCreditNoteListApp {

	@Override
	protected String getHeaderText() {
		return service.getUnvalidatedHeaderText();
	}

	@Override
	public void refresh() {
		table.items(service.listUnvalidated());
		refreshTitleAndHeader();
		goToDefaultFocus();
	}
}
