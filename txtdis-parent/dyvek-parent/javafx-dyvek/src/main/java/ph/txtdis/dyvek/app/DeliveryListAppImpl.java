package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.DeliveryService;

@Scope("prototype")
@Component("deliveryListApp")
public class DeliveryListAppImpl //
		extends AbstractSearchedDeliveryListApp<DeliveryService> //
		implements DeliveryListApp {

	@Override
	protected String headerAndTitleTextPrefix() {
		return "";
	}
}
