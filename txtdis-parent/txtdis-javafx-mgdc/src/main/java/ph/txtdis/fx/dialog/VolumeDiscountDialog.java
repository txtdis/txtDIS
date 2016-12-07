package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.INTEGER;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Channel;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ChannelService;
import ph.txtdis.service.ItemService;
import ph.txtdis.type.UomType;
import ph.txtdis.type.VolumeDiscountType;

@Scope("prototype")
@Component("volumeDiscountDialog")
public class VolumeDiscountDialog extends AbstractFieldDialog<VolumeDiscount> {

	@Autowired
	private ChannelService channelService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private LabeledCombo<UomType> uomCombo;

	@Autowired
	private LabeledCombo<VolumeDiscountType> typeCombo;

	@Autowired
	private LabeledField<BigDecimal> discountField;

	@Autowired
	private LabeledField<Integer> cutoffField;

	@Autowired
	private LabeledCombo<Channel> channelLimitCombo;

	@Autowired
	private LabeledDatePicker startDatePicker;

	private VolumeDiscount discount;

	private void createDiscountUponValidation() {
		if (startDatePicker.getValue() == null)
			return;
		try {
			discount = itemService.createDiscountUponValidation(//
					typeCombo.getValue(), //
					uomCombo.getValue(), //
					cutoffField.getValue(), //
					discountField.getValue(), //
					channelLimitCombo.getValue(), //
					startDatePicker.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(typeCombo(), uomCombo(), cutoffField(), discountField(), channelLimitCombo(), startDatePicker());
	}

	private List<Channel> channels() {
		try {
			return channelService.listAllChannels();
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	private InputNode<?> typeCombo() {
		return typeCombo.name("Type").items(VolumeDiscountType.values()).build();
	}

	private InputNode<?> uomCombo() {
		return uomCombo.name("UOM").items(itemService.listSellingUoms()).build();
	}

	private InputNode<?> cutoffField() {
		return cutoffField.name("Level").build(INTEGER);
	}

	private InputNode<?> discountField() {
		return discountField.name("Discount").build(CURRENCY);
	}

	protected InputNode<?> channelLimitCombo() {
		return channelLimitCombo.name("Only for").items(channels()).build();
	}

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.setOnAction(value -> createDiscountUponValidation());
		return startDatePicker;
	}

	@Override
	protected VolumeDiscount createEntity() {
		return discount;
	}

	@Override
	protected String headerText() {
		return "Add New Discount";
	}
}