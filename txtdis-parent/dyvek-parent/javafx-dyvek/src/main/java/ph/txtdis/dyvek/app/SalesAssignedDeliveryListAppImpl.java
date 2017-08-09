package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.ClientBillAssignmentService;

@Scope("prototype")
@Component("salesAssignedDeliveryListApp")
public class SalesAssignedDeliveryListAppImpl
	extends AbstractAssignedDeliveryListApp<ClientBillAssignmentService>
	implements SalesAssignedDeliveryListApp {
}
