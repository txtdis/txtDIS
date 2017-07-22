package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.VendorBillingService;

@Scope("prototype")
@Component("purchaseAssignedDeliveryListApp")
public class PurchaseAssignedDeliveryListAppImpl //
		extends AbstractAssignedDeliveryListApp<VendorBillingService> //
		implements PurchaseAssignedDeliveryListApp {
}
