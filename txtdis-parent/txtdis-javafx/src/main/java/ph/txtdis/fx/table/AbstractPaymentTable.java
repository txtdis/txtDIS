package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.BillingApp;
import ph.txtdis.dto.RemittanceDetail;

public abstract class AbstractPaymentTable //
		extends AbstractTable<RemittanceDetail> //
		implements AppTable<RemittanceDetail> {

	@Autowired
	private BillingApp app;

	@Autowired
	private Column<RemittanceDetail, String> orderNo, customer;

	@Autowired
	private Column<RemittanceDetail, BigDecimal> total, payment;

	@Override
	protected List<TableColumn<RemittanceDetail, ?>> addColumns() {
		return asList( //
				orderNo.launches(app).ofType(CODE).width(120).build("S/I(D/R) No.", "orderNo"), //
				customer.launches(app).ofType(TEXT).width(320).build("Customer", "customer"), //
				total.launches(app).ofType(CURRENCY).build("Amount Due", "totalDueValue"), //
				payment.launches(app).ofType(CURRENCY).build("Payment", "paymentValue"));
	}
}
