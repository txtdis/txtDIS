package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.math.BigDecimal.ZERO;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.dto.Billing;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.PaymentDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.BillingService;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("paymentDialog")
public class PaymentDialog extends FieldDialog<PaymentDetail> {

	@Autowired
	private LabeledField<Long> idField;

	@Autowired
	private LabeledField<String> customerDisplay;

	@Autowired
	private LabeledField<LocalDate> dateDueDisplay;

	@Autowired
	private LabeledField<BigDecimal> receivableDisplay;

	@Autowired
	private LabeledField<BigDecimal> paymentDisplay;

	@Autowired
	private LabeledField<BigDecimal> balanceDisplay;

	@Autowired
	private LabeledField<BigDecimal> remainingDisplay;

	@Autowired
	private RemittanceService remitService;

	@Autowired
	private BillingService billingService;

	private Billing billable;

	public String moduleId(Long id) {
		return "S/O No. " + id;
	}

	public void payFully(BigDecimal payment, BigDecimal remainingAfterPayment) {
		paymentDisplay.setValue(payment);
		balanceDisplay.setValue(ZERO);
		remainingDisplay.setValue(remainingAfterPayment);
	}

	public void payPartial(BigDecimal unpaidValue, BigDecimal remainingPayment) {
		paymentDisplay.setValue(remainingPayment);
		balanceDisplay.setValue(unpaidValue.subtract(remainingPayment));
		remainingDisplay.setValue(ZERO);
	}

	private LabeledField<BigDecimal> balanceDisplay() {
		return balanceDisplay.name("Balance").readOnly().build(CURRENCY);
	}

	private LabeledField<String> customerDisplay() {
		return customerDisplay.name("Customer").readOnly().build(TEXT);
	}

	private String customerName(Billing dr) {
		Customer c = dr.getCustomer();
		return c == null ? null : c.getName();
	}

	private LabeledField<LocalDate> dateDueDisplay() {
		return dateDueDisplay.name("Date Due").readOnly().build(DATE);
	}

	private LabeledField<Long> idField() {
		idField.name("ID No.").build(ID);
		idField.setOnAction(e -> updateUponDeliveryIdValidation());
		return idField;
	}

	private LabeledField<BigDecimal> paymentField() {
		return paymentDisplay.name("Payment").readOnly().build(CURRENCY);
	}

	private LabeledField<BigDecimal> receivableDisplay() {
		return receivableDisplay.name("Amount Due").readOnly().build(CURRENCY);
	}

	private LabeledField<BigDecimal> remainingDisplay() {
		remainingDisplay.name("Remaining Payment").readOnly().build(CURRENCY);
		remainingDisplay.setValue(remitService.get().getValue());
		return remainingDisplay;
	}

	private void updateBillableDisplays(Billing dr) {
		customerDisplay.setValue(customerName(dr));
		receivableDisplay.setValue(dr.getTotalValue());
		balanceDisplay.setValue(dr.getUnpaidValue());
		updatePaymentDisplays(dr);
	}

	private void updatePaymentDisplays(Billing dr) {
		BigDecimal remainingPayment = remitService.getRemaining();
		BigDecimal unpaidValue = dr.getUnpaidValue();
		BigDecimal remainingAfterPayment = remainingPayment.subtract(unpaidValue);

		if (isPositive(remainingAfterPayment))
			payFully(unpaidValue, remainingAfterPayment);
		else
			payPartial(unpaidValue, remainingPayment);
	}

	private void updateUponDeliveryIdValidation() {
		try {
			updateUponIdValidation();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void updateUponIdValidation() throws Exception {
		Long id = idField.getValue();
		Billing i = billingService.getBillable(id);
		if (i == null)
			throw new NotFoundException("S/O No." + id);
		if (remitService.foundThisBillableOnThisPaymentList(i))
			if (isZero(i.getUnpaidValue()))
				throw new Exception("S/O No." + id + "\nis fully paid");
		updateBillableDisplays(i);
		billable = i;

	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return Arrays.asList(//
				idField(), //
				customerDisplay(), //
				dateDueDisplay(), //
				receivableDisplay(), //
				paymentField(), //
				balanceDisplay(), //
				remainingDisplay());
	}

	@Override
	protected PaymentDetail createEntity() {
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
