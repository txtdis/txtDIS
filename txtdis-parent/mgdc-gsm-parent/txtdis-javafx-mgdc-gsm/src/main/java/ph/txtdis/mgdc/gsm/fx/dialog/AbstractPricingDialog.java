package ph.txtdis.mgdc.gsm.fx.dialog;

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
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;

public abstract class AbstractPricingDialog //
		extends AbstractFieldDialog<Price> //
		implements PricingDialog {

	@Autowired
	protected LabeledCombo<PricingType> typeCombo;

	@Autowired
	protected LabeledDatePicker startDatePicker;

	@Autowired
	protected LabeledField<BigDecimal> priceField;

	@Autowired
	protected BommedDiscountedPricedValidatedItemService itemService;

	protected Price price;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(typeCombo(), priceField(), startDatePicker());
	}

	private LabeledCombo<PricingType> typeCombo() {
		return typeCombo.name("Type").items(itemService.listPricingTypes()).build();
	}

	protected LabeledField<BigDecimal> priceField() {
		return priceField.name("Price").build(CURRENCY);
	}

	protected LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.onAction(value -> createPricingUponValidation());
		return startDatePicker;
	}

	protected abstract void createPricingUponValidation();

	@Override
	protected Price createEntity() {
		return price;
	}

	@Override
	protected String headerText() {
		return "Add New Pricing";
	}
}