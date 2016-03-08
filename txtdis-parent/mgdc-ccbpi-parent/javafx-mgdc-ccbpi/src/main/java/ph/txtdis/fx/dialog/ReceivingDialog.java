package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.INTEGER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.ItemList;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.BillingService;

@Scope("prototype")
@Component("receivingDialog")
public class ReceivingDialog extends FieldDialog<ItemList> {

	@Autowired
	private LabeledCombo<String> itemCombo;

	@Autowired
	private LabeledField<Integer> caseField;

	@Autowired
	private LabeledField<Integer> bottleField;

	@Autowired
	private BillingService service;

	@Override
	public void refresh() {
		super.refresh();
		itemCombo.items(service.getReceivableItemNames());
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(//
				itemCombo.name("Item").build(), //
				caseField.name("No. of Cases").build(INTEGER), //
				bottleField.name("No. of Bottles").build(INTEGER));
	}

	@Override
	protected ItemList createEntity() {
		return service.updateReturnedQty(caseField.getValue(), bottleField.getValue());
	}

	@Override
	protected String headerText() {
		return "Receive an Item";
	}
}
