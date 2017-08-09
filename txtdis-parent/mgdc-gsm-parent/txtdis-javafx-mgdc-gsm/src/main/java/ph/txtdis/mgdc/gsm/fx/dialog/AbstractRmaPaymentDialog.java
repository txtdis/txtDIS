package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.gsm.service.CreditedAndDiscountedCustomerService;
import ph.txtdis.mgdc.gsm.service.RefundedRmaService;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.ID;

public abstract class AbstractRmaPaymentDialog<RRS extends RefundedRmaService> //
	extends AbstractFieldDialog<LocalDate> {

	@Autowired
	private LabeledCombo<String> bankCombo;

	@Autowired
	private LabeledField<String> prefixField, suffixField;

	@Autowired
	private LabeledField<Long> idField, checkIdField;

	@Autowired
	private LabeledDatePicker datePicker;

	@Autowired
	private CreditedAndDiscountedCustomerService bankService;

	@Autowired
	private RRS rmaService;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(//
			bankCombo(), //
			checkIdField(), //
			prefixField.name("S/I Code").build(CODE), //
			idField.name("S/I No.").build(ID), //
			suffixField(), //
			datePicker.name("Date")); //
	}

	private LabeledCombo<String> bankCombo() {
		bankCombo.name("Bank").items(bankService.listBanks()).build();
		bankCombo.select(null);
		return bankCombo;
	}

	private LabeledField<Long> checkIdField() {
		checkIdField.name("Check No.").build(ID);
		checkIdField.onAction(e -> updateUponCheckIdValidation());
		return checkIdField;
	}

	private LabeledField<String> suffixField() {
		suffixField.name("S/I Series").width(40).build(CODE);
		suffixField.onAction(e -> updateUponInvoiceNoValidation());
		return suffixField;
	}

	private void updateUponCheckIdValidation() {
		try {
			rmaService.updateUponCheckIdValidation(bankCombo.getValue(), checkIdField.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void updateUponInvoiceNoValidation() {
		try {
			rmaService.updateUponInvoiceNoValidation(prefixField.getValue(), idField.getValue(), suffixField.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected LocalDate createEntity() {
		return datePicker.getValue();
	}

	@Override
	protected BooleanBinding getAddButtonDisableBindings() {
		return bankCombo.isEmpty() //
			.or(checkIdField.isEmpty()) //
			.or(idField.isEmpty()) //
			.or(datePicker.isEmpty());
	}

	@Override
	protected String headerText() {
		return "Pay Returned Item";
	}
}
