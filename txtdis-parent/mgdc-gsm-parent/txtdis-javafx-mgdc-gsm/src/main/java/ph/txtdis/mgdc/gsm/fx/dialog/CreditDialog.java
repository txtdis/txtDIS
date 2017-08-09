package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.gsm.service.CreditedAndDiscountedCustomerService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.INTEGER;

@Scope("prototype")
@Component("creditDialog")
public class CreditDialog //
	extends AbstractFieldDialog<CreditDetail> {

	@Autowired
	private CreditedAndDiscountedCustomerService service;

	@Autowired
	private LabeledField<Integer> termField;

	@Autowired
	private LabeledField<Integer> graceField;

	@Autowired
	private LabeledField<BigDecimal> creditField;

	@Autowired
	private LabeledDatePicker startDatePicker;

	private CreditDetail credit;

	@Override
	protected List<InputNode<?>> addNodes() {
		termField.name("Term Days").build(INTEGER);
		graceField.name("Grace Period Days").build(INTEGER);
		creditField.name("Credit Limit").build(CURRENCY);
		return Arrays.asList(termField, graceField, creditField, startDatePicker());
	}

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.onAction(value -> createCreditLineUponValidation());
		return startDatePicker;
	}

	private void createCreditLineUponValidation() {
		try {
			if (startDatePicker.getValue() != null)
				credit = service
					.createCreditLineUponValidation(termField.getValue(), graceField.getValue(), creditField.getValue(),
						startDatePicker.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected CreditDetail createEntity() {
		return credit;
	}

	@Override
	protected String headerText() {
		return "Add New Credit Details";
	}
}
