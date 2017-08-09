package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Remittance;
import ph.txtdis.fx.table.RemittanceListTableImpl;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("checkListApp")
public class CheckListApp //
	extends AbstractListApp<RemittanceListTableImpl, RemittanceService, Remittance> {

	private String bank;

	public CheckListApp bank(String bank) {
		this.bank = bank;
		return this;
	}

	@Override
	public Long getSelectedKey() {
		Remittance r = table.getItem();
		return r == null ? null : r.getCheckId();
	}

	@Override
	public void refresh() {
		table.items(service.list(bank));
	}

	@Override
	protected String getHeaderText() {
		return "Payment via " + bank + " List";
	}
}
