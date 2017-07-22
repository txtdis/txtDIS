package ph.txtdis.dyvek.fx.dialog;

import static ph.txtdis.type.PartnerType.OUTLET;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.TradingClientService;
import ph.txtdis.type.PartnerType;

@Scope("prototype")
@Component("tradingClientDialog")
public class TradingClientDialogImpl //
		extends AbstractCustomerDialog<TradingClientService> //
		implements TradingClientDialog {

	@Override
	protected PartnerType partnerType() {
		return OUTLET;
	}

	@Override
	protected String name() {
		return "Customer";
	}
}