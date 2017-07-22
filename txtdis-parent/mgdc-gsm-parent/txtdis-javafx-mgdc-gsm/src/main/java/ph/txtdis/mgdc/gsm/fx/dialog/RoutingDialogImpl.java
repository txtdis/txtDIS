package ph.txtdis.mgdc.gsm.fx.dialog;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.fx.dialog.AbstractRoutingDialog;
import ph.txtdis.mgdc.fx.dialog.RoutingDialog;
import ph.txtdis.mgdc.gsm.service.Qualified_CreditAndDiscountGivenCustomerService;

@Scope("prototype")
@Component("routingDialog")
public class RoutingDialogImpl //
		extends AbstractRoutingDialog<Qualified_CreditAndDiscountGivenCustomerService> //
		implements RoutingDialog {

	@Override
	protected List<Route> getRoutes() {
		List<Route> l = super.getRoutes();
		return !customerService.hasCreditOrDiscountBeenGiven() ? l // 
				: l.stream().filter(r -> !r.getName().startsWith(EX_TRUCK.toString())).collect(toList());
	}
}
