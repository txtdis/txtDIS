package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.service.ItemFamilyService;
import ph.txtdis.type.ItemTier;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.ItemTier.values;

@Scope("prototype")
@Component("itemFamilyDialog")
public class ItemFamilyDialog
	extends AbstractNameListDialog<ItemFamily, ItemFamilyService> {

	@Autowired
	private LabeledCombo<ItemTier> tierCombo;

	@Override
	protected List<InputNode<?>> addNodes() {
		super.addNodes();
		tierCombo.name("Tier").items(values()).build();
		return asList(nameField, tierCombo);
	}

	@Override
	protected ItemFamily createEntity() {
		try {
			return service.save(nameField.getValue(), tierCombo.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Item Family";
	}
}
