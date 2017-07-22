package ph.txtdis.dyvek.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.dialog.ClientBillDialogImpl;
import ph.txtdis.dyvek.service.ClientBillAssignmentService;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.info.Information;

@Scope("prototype")
@Component("clientBillAssignmentApp")
public class ClientBillAssignmentAppImpl //
		extends AbstractBillingApp<UnassignedClientDeliveryListApp, SalesAssignedDeliveryListApp, ClientBillAssignmentService> //
		implements ClientBillAssignmentApp {

	@Autowired
	private ClientBillDialogImpl billDialog;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = super.addButtons();
		b.add(billActionButton.icon("adjustment").tooltip("Adjustments...").build());
		return b;
	}

	@Override
	protected String billActedByPrompt() {
		return "Bill Adjusted by";
	}

	@Override
	protected void openBillingDialog() {
		try {
			billDialog.addParent(this).start();
			service.setAdjustments(billDialog.getAddedItems());
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}
}
