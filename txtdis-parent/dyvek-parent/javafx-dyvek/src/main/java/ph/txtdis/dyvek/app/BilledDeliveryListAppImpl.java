package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.ClientBillingService;

@Scope("prototype")
@Component("billedDeliveryListApp")
public class BilledDeliveryListAppImpl //
		extends AbstractSearchedDeliveryListApp<ClientBillingService> //
		implements BilledDeliveryListApp {

	@Override
	protected String headerAndTitleTextPrefix() {
		return "Billed";
	}
}
