package ph.txtdis.dyvek.app;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.ClientBillDialog;
import ph.txtdis.dyvek.service.ClientBillAssignmentService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.info.Information;

import java.util.List;

@Scope("prototype")
@Component("clientBillAssignmentApp")
public class ClientBillAssignmentAppImpl
	extends AbstractBillingApp<UnassignedClientDeliveryListApp, SalesAssignedDeliveryListApp,
	ClientBillAssignmentService>
	implements ClientBillAssignmentApp {

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.add(billActionButton = button.icon("adjustment").tooltip("Adjustments...").build());
		return b;
	}

	@Override
	protected String billActedByPrompt() {
		return "Bill Adjusted by";
	}

	@Override
	@Lookup("unassignedClientDeliveryListApp")
	protected UnassignedClientDeliveryListApp openOrderListApp() {
		return null;
	}

	@Override
	protected void openBillingDialog() {
		try {
			ClientBillDialog d = billDialog();
			d.addParent(this).start();
			service.setAdjustments(d.getAddedItems());
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Lookup
	ClientBillDialog billDialog() {
		return null;
	}

	@Override
	@Lookup("salesAssignedDeliveryListApp")
	protected SalesAssignedDeliveryListApp orderListApp() {
		return null;
	}
}
