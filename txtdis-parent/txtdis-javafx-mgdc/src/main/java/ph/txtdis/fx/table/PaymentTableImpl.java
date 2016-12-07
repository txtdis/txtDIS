package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.BillingApp;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.fx.dialog.PaymentDialog;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("paymentTable")
public class PaymentTableImpl extends AbstractTableView<RemittanceDetail> implements PaymentTable {

	@Autowired
	private BillingApp app;

	@Autowired
	private AppendContextMenu<RemittanceDetail> append;

	@Autowired
	private Column<RemittanceDetail, String> orderNo;

	@Autowired
	private Column<RemittanceDetail, String> customer;

	@Autowired
	private Column<RemittanceDetail, LocalDate> dueDate;

	@Autowired
	private Column<RemittanceDetail, BigDecimal> total;

	@Autowired
	private Column<RemittanceDetail, BigDecimal> payment;

	@Autowired
	private PaymentDialog dialog;

	@Autowired
	private RemittanceService service;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				orderNo.launches(app).ofType(CODE).width(120).build("S/I(D/R) No.", "orderNo"), //
				customer.launches(app).ofType(TEXT).width(320).build("Customer", "customerName"), //
				dueDate.launches(app).ofType(DATE).build("Due Date", "dueDate"), //
				total.launches(app).ofType(CURRENCY).build("Amount Due", "totalDueValue"), //
				payment.launches(app).ofType(CURRENCY).build("Payment", "paymentValue"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
	}
}
