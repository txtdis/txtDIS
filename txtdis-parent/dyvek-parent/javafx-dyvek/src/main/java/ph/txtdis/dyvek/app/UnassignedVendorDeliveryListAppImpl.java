package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.VendorBillingService;

@Scope("prototype")
@Component("unassignedVendorDeliveryListApp")
public class UnassignedVendorDeliveryListAppImpl //
		extends AbstractDeliveryListApp<VendorBillingService> //
		implements UnassignedVendorDeliveryListApp {

	@Override
	protected String headerAndTitleTextPrefix() {
		return "Unassigned";
	}
}
