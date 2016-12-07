package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.CustomerService;

public abstract class AbstractCustomerDiscountDialog extends AbstractFieldDialog<CustomerDiscount>
		implements CustomerDiscountDialog {

	@Autowired
	protected CustomerService service;

	@Autowired
	protected LabeledField<BigDecimal> discountField;

	@Autowired
	protected LabeledDatePicker startDatePicker;

	private CustomerDiscount customerDiscount;

	protected LabeledField<BigDecimal> discountField(String label) {
		return discountField.name(label).build(CURRENCY);
	}

	protected LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.setOnAction(value -> createDiscountUponValidation());
		return startDatePicker;
	}

	private void createDiscountUponValidation() {
		if (startDatePicker.getValue() != null)
			try {
				customerDiscount = validateThenCreateDiscount();
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	protected abstract CustomerDiscount validateThenCreateDiscount() throws DateInThePastException, DuplicateException;

	@Override
	protected CustomerDiscount createEntity() {
		return customerDiscount;
	}

	@Override
	protected String headerText() {
		return "Add New Discount";
	}
}