package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.ccbpi.fx.table.CustomerListTable;
import ph.txtdis.mgdc.ccbpi.service.ItemDeliveredCustomerService;

@Scope("prototype")
@Component("customerListApp")
public class CustomerListAppImpl //
		extends AbstractExcelApp<CustomerListTable, ItemDeliveredCustomerService, Customer> //
		implements CustomerListApp {

	@Override
	public Customer getSelection() {
		return table.getItem();
	}

	@Override
	public void start() {
		setStage(mainVerticalPane());
		table.setItem(null);
		refresh();
		showAndWait();
	}

	@Override
	protected String getHeaderText() {
		return "Customer List";
	}

	@Override
	protected String getTitleText() {
		return getHeaderText();
	}
}
