package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.service.WarehouseService;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("warehouseDialog")
public class WarehouseDialog
	extends AbstractNameListDialog<Warehouse, WarehouseService> {

	@Autowired
	private LabeledCombo<ItemFamily> familyCombo;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(super.addNodes());
		l.add(familyCombo.name("Storage for").items(service.listAllFamilies()).build());
		return l;
	}

	@Override
	protected Warehouse createEntity() {
		try {
			return service.save(nameField.getValue(), familyCombo.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Warehouse";
	}
}