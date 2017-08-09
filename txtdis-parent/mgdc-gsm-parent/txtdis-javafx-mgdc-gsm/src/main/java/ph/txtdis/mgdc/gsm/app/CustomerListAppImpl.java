package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.fx.table.CustomerListTable;
import ph.txtdis.mgdc.gsm.service.ItemDeliveredCustomerService;

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
	protected String getTitleText() {
		return getHeaderText();
	}

	@Override
	protected String getHeaderText() {
		return "Customer List";
	}
}
