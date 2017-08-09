package ph.txtdis.mgdc.ccbpi.app;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.excel.OCPReader;
import ph.txtdis.mgdc.ccbpi.fx.dialog.OCPChooser;
import ph.txtdis.mgdc.ccbpi.fx.table.DeliveryListTable;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListService;

@Scope("prototype")
@Component("deliveryListApp")
public class DeliveryListAppImpl //
	extends AbstractShippedBillableApp<DeliveryListService, DeliveryListTable> //
	implements DeliveryListApp {

	@Autowired
	private AppCombo<String> routeCombo;

	@Autowired
	private OCPChooser ocpChooser;

	@Autowired
	private OCPReader ocpReader;

	@Override
	protected void renew() {
		super.renew();
		routeCombo.select(service.getRoute());
		orderDatePicker.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		routeCombo.disableIf(orderDatePicker.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		orderDatePicker.onAction(e -> service.setOrderDate(orderDatePicker.getValue()));
		routeCombo.onAction(e -> updateBasedOnSelectedRoute(routeCombo.getValue()));
	}

	void updateBasedOnSelectedRoute(String route) {
		if (service.isNew() && route != null)
			try {
				updateBasedOnRouteSelection(route);
			} catch (Information i) {
				messageDialog.show(i).addParent(this).start();
				refresh();
			} catch (Exception e) {
				showErrorDialog(e);
			}
	}

	private void updateBasedOnRouteSelection(String route) throws Information, Exception {
		if (route.equalsIgnoreCase("IMPORT"))
			importOCPUponOrderDateValidation();
		else
			service.updateUponRouteValidation(route);
	}

	@Override
	public void refresh() {
		super.refresh();
		routeCombo.items(service.listRouteNames());
	}

	private void importOCPUponOrderDateValidation() throws Information, Exception {
		service.confirmNoDeliveryListOfSameDateExists();
		importOCP();
	}

	private void importOCP() throws Information, Exception {
		File ocp = ocpChooser.showOpenDialog(this);
		if (ocp != null)
			ocpReader.importOCP(ocp);
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(routeCombo.isEmpty());
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		gridPane.add(label.field(service.getRoutePrompt()), 3, 0);
		gridPane.add(routeCombo, 4, 0);
	}
}
