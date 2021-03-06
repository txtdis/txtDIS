package ph.txtdis.mgdc.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.service.CreditNoteService;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("creditNotePaymentDialog")
public class CreditNotePaymentDialog //
	extends AbstractFieldDialog<CreditNotePayment> {

	@Autowired
	private CreditNoteService service;

	@Autowired
	private LabeledDatePicker datePicker;

	@Autowired
	private LabeledField<String> referenceField, remarksField;

	@Autowired
	private LabeledField<BigDecimal> paymentField;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(//
			datePicker.name("Payment Date"), //
			referenceField.name("Reference").build(TEXT), //
			paymentField.name("Payment").build(CURRENCY), //
			remarksField.name("Remarks").build(TEXT));
	}

	@Override
	protected CreditNotePayment createEntity() {
		return service.createPayment(//
			datePicker.getValue(), //
			referenceField.getValue(), //
			paymentField.getValue(), //
			remarksField.getValue());
	}

	@Override
	protected String headerText() {
		return "Add New Payment";
	}
}
