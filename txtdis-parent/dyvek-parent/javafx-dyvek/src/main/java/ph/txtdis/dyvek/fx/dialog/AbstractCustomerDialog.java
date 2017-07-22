package ph.txtdis.dyvek.fx.dialog;

import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.CustomerService;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.type.PartnerType;

public abstract class AbstractCustomerDialog<CS extends CustomerService> //
		extends AbstractNameListDialog<Customer, CS> {

	@Override
	protected Customer createEntity() {
		try {
			return service.save(nameField.getValue(), partnerType());
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	protected abstract PartnerType partnerType();

	@Override
	protected String headerText() {
		return "Add New " + name();
	}

	protected abstract String name();
}