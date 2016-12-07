package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.INTEGER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.PercentBasedCustomerDiscountService;

@Scope("prototype")
@Component("customerDiscountDialog")
public class CustomerDiscountDialogImpl extends AbstractCustomerDiscountDialog {

	@Autowired
	private PercentBasedCustomerDiscountService percentBasedCustomerDiscountService;

	@Autowired
	private LabeledField<Integer> levelField;

	@Autowired
	private LabeledCombo<ItemFamily> familyLimitCombo;

	@Override
	protected List<InputNode<?>> addNodes() {
		levelField.name("Level").build(INTEGER);
		familyLimitCombo.name("Only for").items(service.listAllFamilies()).build();
		return asList(levelField, discountField("% Discount"), familyLimitCombo, startDatePicker());
	}

	@Override
	protected CustomerDiscount validateThenCreateDiscount() throws DateInThePastException, DuplicateException {
		return percentBasedCustomerDiscountService.createDiscountUponValidation(//
				levelField.getValue(), //
				discountField.getValue(), //
				familyLimitCombo.getValue(), //
				startDatePicker.getValue());
	}
}