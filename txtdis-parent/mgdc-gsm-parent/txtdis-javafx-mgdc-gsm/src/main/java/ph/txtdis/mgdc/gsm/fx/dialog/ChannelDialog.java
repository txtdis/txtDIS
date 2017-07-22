package ph.txtdis.mgdc.gsm.fx.dialog;

import static ph.txtdis.type.BillingType.values;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCheckBox;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.service.ChannelService;
import ph.txtdis.type.BillingType;

@Scope("prototype")
@Component("channelDialog")
public class ChannelDialog extends AbstractNameListDialog<Channel, ChannelService> {

	@Autowired
	private LabeledCombo<BillingType> typeCombo;

	@Autowired
	private LabeledCheckBox visitedCheckBox;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(super.addNodes());
		l.add(typeCombo.name("Billing Type").items(values()).build());
		l.add(visitedCheckBox.name("Visited").build());
		return l;
	}

	@Override
	protected Channel createEntity() {
		try {
			return service.save(//
					nameField.getValue(), //
					typeCombo.getValue(), //
					visitedCheckBox.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Channel";
	}
}
