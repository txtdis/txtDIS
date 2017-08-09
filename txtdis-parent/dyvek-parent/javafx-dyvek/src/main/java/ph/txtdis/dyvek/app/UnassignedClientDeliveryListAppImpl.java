package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.ClientBillAssignmentService;

@Scope("prototype")
@Component("unassignedClientDeliveryListApp")
public class UnassignedClientDeliveryListAppImpl
	extends AbstractDeliveryListApp<ClientBillAssignmentService>
	implements UnassignedClientDeliveryListApp {

	@Override
	protected String headerAndTitleTextPrefix() {
		return "Unassigned";
	}
}
