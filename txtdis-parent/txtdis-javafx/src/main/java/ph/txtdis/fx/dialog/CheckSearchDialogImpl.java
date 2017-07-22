package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Remittance;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("checkSearchDialog")
public class CheckSearchDialogImpl //
		extends AbstractCheckDialog<Remittance, RemittanceService> {

	private static final String SEARCH = "Search";

	@Override
	protected String addButtonLabelName() {
		return SEARCH;
	}

	@Override
	protected String headerText() {
		return SEARCH + " for a Check";
	}

	@Override
	protected Remittance createEntity() {
		return null;
	}
}
