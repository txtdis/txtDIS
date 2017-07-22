package ph.txtdis.dyvek.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.BankService;
import ph.txtdis.type.PartnerType;

@Scope("prototype")
@Component("bankDialog")
public class BankDialogImpl //
		extends AbstractCustomerDialog<BankService> //
		implements BankDialog {

	@Override
	protected PartnerType partnerType() {
		return PartnerType.FINANCIAL;
	}

	@Override
	protected String name() {
		return "Bank";
	}
}