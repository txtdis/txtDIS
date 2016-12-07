package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ItemService;

public abstract class AbstractPricingDialog extends AbstractFieldDialog<Price> implements PricingDialog {

	@Autowired
	protected LabeledCombo<PricingType> typeCombo;

	@Autowired
	protected LabeledDatePicker startDatePicker;

	@Autowired
	protected LabeledField<BigDecimal> priceField;

	@Autowired
	protected ItemService itemService;

	protected Price price;

	protected abstract void createPricingUponValidation();

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.setOnAction(value -> createPricingUponValidation());
		return startDatePicker;
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		typeCombo.name("Type").items(itemService.listPricingTypes()).build();
		priceField.name("Price").build(CURRENCY);
		return asList(typeCombo, priceField, startDatePicker());
	}

	@Override
	protected Price createEntity() {
		return price;
	}

	@Override
	protected String headerText() {
		return "Add New Pricing";
	}
}