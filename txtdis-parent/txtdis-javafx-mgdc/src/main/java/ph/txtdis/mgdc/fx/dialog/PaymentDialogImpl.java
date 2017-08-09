package ph.txtdis.mgdc.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.service.AdjustableInputtedPaymentDetailedRemittanceService;
import ph.txtdis.type.BillingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.type.BillingType.DELIVERY;
import static ph.txtdis.type.BillingType.INVOICE;
import static ph.txtdis.type.Type.*;
import static ph.txtdis.util.NumberUtils.isPositive;

@Scope("prototype")
@Component("paymentDialog")
public class PaymentDialogImpl
	extends AbstractFieldDialog<RemittanceDetail>
	implements PaymentDialog {

	@Autowired
	private LabeledCombo<BillingType> billableCombo;

	@Autowired
	private LabeledField<BigDecimal> receivableDisplay, paymentDisplay, balanceDisplay, remainingDisplay;

	@Autowired
	private LabeledField<LocalDate> dateDueDisplay;

	@Autowired
	private LabeledField<Long> idField;

	@Autowired
	private LabeledField<String> prefixField, suffixField, customerDisplay;

	@Autowired
	private AdjustableInputtedPaymentDetailedRemittanceService remitService;

	private Billable billable;

	@Override
	protected List<InputNode<?>> addNodes() {
		return Arrays.asList(//
			billableCombo(), //
			prefixField(), //
			idField(), //
			suffixField(), //
			customerDisplay(), //
			dateDueDisplay(), //
			receivableDisplay(), //
			paymentField(), //
			balanceDisplay(), //
			remainingDisplay());
	}

	private LabeledCombo<BillingType> billableCombo() {
		billableCombo.name("Type").items(BillingType.values()).build();
		billableCombo.select(null);
		return billableCombo;
	}

	private void updateUponDeliveryIdValidation() {
		try {
			BillingType b = billableCombo.getValue();
			if (b != null && b.equals(DELIVERY))
				updateUponIdValidation(DELIVERY);
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void updateUponIdValidation(BillingType b) throws Exception {
		Billable i = remitService.updateUponIdValidation(b, //
			prefixField.getValue(), //
			idField.getValue(), //
			suffixField.getValue());
		updateBillableDisplays(billable = i);
	}

	private void updateBillableDisplays(Billable i) {
		customerDisplay.setValue(i.getCustomerName());
		dateDueDisplay.setValue(i.getDueDate());
		receivableDisplay.setValue(i.getTotalValue());
		balanceDisplay.setValue(i.getUnpaidValue());
		updatePaymentDisplays(i);
	}

	private void updatePaymentDisplays(Billable i) {
		BigDecimal remainingPayment = remitService.getRemaining();
		BigDecimal unpaidValue = i.getUnpaidValue();
		BigDecimal remainingAfterPayment = remainingPayment.subtract(unpaidValue);

		if (isPositive(remainingAfterPayment))
			payFully(unpaidValue, remainingAfterPayment);
		else
			payPartial(unpaidValue, remainingPayment);
	}

	private void payFully(BigDecimal payment, BigDecimal remainingAfterPayment) {
		paymentDisplay.setValue(payment);
		balanceDisplay.setValue(ZERO);
		remainingDisplay.setValue(remainingAfterPayment);
	}

	private void payPartial(BigDecimal unpaidValue, BigDecimal remainingPayment) {
		paymentDisplay.setValue(remainingPayment);
		balanceDisplay.setValue(unpaidValue.subtract(remainingPayment));
		remainingDisplay.setValue(ZERO);
	}

	private LabeledField<String> prefixField() {
		prefixField.name("Code").width(70).build(TEXT);
		prefixField.disableIf(billableCombo.is(DELIVERY));
		return prefixField;
	}

	private LabeledField<Long> idField() {
		idField.name("ID No.").build(ID);
		idField.onAction(e -> updateUponDeliveryIdValidation());
		return idField;
	}

	private LabeledField<String> suffixField() {
		suffixField.name("Series").width(40).build(TEXT);
		suffixField.onAction(e -> updateUponInvoiceIdValidation());
		suffixField.disableIf(billableCombo.is(DELIVERY));
		return suffixField;
	}

	private void updateUponInvoiceIdValidation() {
		try {
			updateUponIdValidation(INVOICE);
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private LabeledField<String> customerDisplay() {
		return customerDisplay.name("Customer").readOnly().build(TEXT);
	}

	private LabeledField<LocalDate> dateDueDisplay() {
		return dateDueDisplay.name("Date Due").readOnly().build(DATE);
	}

	private LabeledField<BigDecimal> receivableDisplay() {
		return receivableDisplay.name("Amount Due").readOnly().build(CURRENCY);
	}

	private LabeledField<BigDecimal> paymentField() {
		return paymentDisplay.name("Payment").readOnly().build(CURRENCY);
	}

	private LabeledField<BigDecimal> balanceDisplay() {
		return balanceDisplay.name("Balance").readOnly().build(CURRENCY);
	}

	private LabeledField<BigDecimal> remainingDisplay() {
		remainingDisplay.name("Remaining Payment").readOnly().build(CURRENCY);
		remainingDisplay.setValue(remitService.getValue());
		return remainingDisplay;
	}

	@Override
	protected RemittanceDetail createEntity() {
		return remitService.createDetail(billable, paymentDisplay.getValue(), remainingDisplay.getValue());
	}

	@Override
	protected BooleanBinding getAddButtonDisableBindings() {
		return receivableDisplay.isEmpty().or(paymentDisplay.isEmpty());
	}

	@Override
	protected String headerText() {
		return "Pay a Billable";
	}
}
