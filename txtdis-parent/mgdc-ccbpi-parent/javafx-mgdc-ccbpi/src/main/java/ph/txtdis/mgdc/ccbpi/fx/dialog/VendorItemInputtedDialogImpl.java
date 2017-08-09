package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.dto.Item;

@Scope("prototype")
@Component("itemInputtedDialog")
public class VendorItemInputtedDialogImpl //
	extends AbstractItemInputtedDialog {

	@Override
	public Item validateItemExists() throws Exception {
		Item i = itemService.findByVendorNo(getId().toString());
		itemNameDisplay.setValue(i.getName());
		return i;
	}
}
