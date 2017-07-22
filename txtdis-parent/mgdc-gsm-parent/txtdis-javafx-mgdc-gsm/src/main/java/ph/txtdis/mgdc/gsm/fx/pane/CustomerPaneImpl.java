package ph.txtdis.mgdc.gsm.fx.pane;

import static ph.txtdis.type.PartnerType.EX_TRUCK;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("customerPane")
public class CustomerPaneImpl //
		extends AbstractCustomerPane {

	@Override
	public void refresh() {
		super.refresh();
		String name = nameField.getValue();
		if (name != null && name.startsWith(EX_TRUCK.toString()))
			parentIdField.disable();
	}

	@Override
	public void setBindings() {
		super.setBindings();
		typeCombo.disableIf(barangayCombo.isEmpty().and(typeCombo.isNot(EX_TRUCK)));
		channelCombo.disableIf(typeCombo.isEmpty().or(typeCombo.is(EX_TRUCK)));
	}
}
