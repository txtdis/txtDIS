package ph.txtdis.dyvek.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.service.TruckingClientService;
import ph.txtdis.type.PartnerType;

import static ph.txtdis.type.PartnerType.EX_TRUCK;

@Scope("prototype")
@Component("truckingClientDialog")
public class TruckingClientDialogImpl
	extends AbstractCustomerDialog<TruckingClientService>
	implements TruckingClientDialog {

	@Override
	protected PartnerType partnerType() {
		return EX_TRUCK;
	}

	@Override
	protected String name() {
		return "Customer";
	}
}