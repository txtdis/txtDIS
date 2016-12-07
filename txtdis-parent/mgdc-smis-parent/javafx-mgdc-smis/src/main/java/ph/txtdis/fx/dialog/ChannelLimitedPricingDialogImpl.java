package ph.txtdis.fx.dialog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Channel;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.service.ChannelService;

@Scope("prototype")
@Component("pricingDialog")
public class ChannelLimitedPricingDialogImpl extends AbstractPricingDialog {

	@Autowired
	private ChannelService channelService;

	@Autowired
	private LabeledCombo<Channel> channelLimitCombo;

	private List<Channel> channels() {
		try {
			return channelService.listAllChannels();
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected void createPricingUponValidation() {
		if (startDatePicker.getValue() != null)
			try {
				price = itemService.createPricingUponValidation(//
						typeCombo.getValue(), //
						priceField.getValue(), //
						channelLimitCombo.getValue(), //
						startDatePicker.getValue());
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		channelLimitCombo.name("Only for").items(channels()).build();
		List<InputNode<?>> nodes = new ArrayList<>(super.addNodes());
		nodes.add(2, channelLimitCombo);
		return nodes;
	}
}