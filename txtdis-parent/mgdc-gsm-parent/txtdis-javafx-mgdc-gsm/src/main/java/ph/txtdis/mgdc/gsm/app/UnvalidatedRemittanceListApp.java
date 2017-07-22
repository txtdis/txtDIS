package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractListApp;
import ph.txtdis.dto.Remittance;
import ph.txtdis.fx.table.RemittanceListTable;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("unvalidatedRemittanceListApp")
public class UnvalidatedRemittanceListApp //
		extends AbstractListApp<RemittanceListTable, RemittanceService, Remittance> {

	@Override
	protected String getHeaderText() {
		return "Unvalidated " + super.getHeaderText();
	}
}
