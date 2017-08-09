package ph.txtdis.dyvek.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.dyvek.service.ItemService;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.info.Information;

@Scope("prototype")
@Component("itemDialog")
public class ItemDialogImpl
	extends AbstractNameListDialog<Item, ItemService> {

	@Override
	protected Item createEntity() {
		try {
			return service.save(nameField.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Item";
	}
}