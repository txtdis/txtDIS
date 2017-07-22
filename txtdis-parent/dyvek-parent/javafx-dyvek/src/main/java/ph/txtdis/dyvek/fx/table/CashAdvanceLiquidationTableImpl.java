package ph.txtdis.dyvek.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

@Scope("prototype")
@Component("cashAdvanceLiquidationTable")
public class CashAdvanceLiquidationTableImpl //
		extends AbstractTable<RemittanceDetail> //
		implements CashAdvanceLiquidationTable {

	@Autowired
	private Column<RemittanceDetail, LocalDate> orderDate;

	@Autowired
	private Column<RemittanceDetail, String> orderNo, customer;

	@Autowired
	private Column<RemittanceDetail, BigDecimal> payment;

	@Autowired
	private DialogClosingOnlyApp app;

	@Override
	protected List<TableColumn<RemittanceDetail, ?>> addColumns() {
		return asList( //
				orderDate.ofType(DATE).launches(app).build("Date", "orderDate"), //
				orderNo.ofType(TEXT).launches(app).width(90).build("D/R No.", "orderNo"), //
				customer.ofType(TEXT).launches(app).build("Supplier/Customer", "customer"), //
				payment.ofType(CURRENCY).launches(app).build("Amount", "paymentValue"));
	}
}
