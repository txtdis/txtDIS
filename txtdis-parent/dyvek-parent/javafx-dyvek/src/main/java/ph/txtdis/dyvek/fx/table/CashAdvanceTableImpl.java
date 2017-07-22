package ph.txtdis.dyvek.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dyvek.app.CashAdvanceLiquidationApp;
import ph.txtdis.dyvek.fx.dialog.CashAdvanceDialog;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;

@Scope("prototype")
@Component("cashAdvanceTable")
public class CashAdvanceTableImpl //
		extends AbstractTable<CashAdvance> //
		implements CashAdvanceTable {

	@Autowired
	private AppendContextMenu<CashAdvance> append;

	@Autowired
	private Column<CashAdvance, BigDecimal> totalValue, balanceValue;

	@Autowired
	private Column<CashAdvance, LocalDate> checkDate;

	@Autowired
	private Column<CashAdvance, Long> id, checkId;

	@Autowired
	private Column<CashAdvance, String> customer, bank;

	@Autowired
	private CashAdvanceDialog dialog;

	@Autowired
	private CashAdvanceLiquidationApp app;

	@Override
	protected List<TableColumn<CashAdvance, ?>> addColumns() {
		return asList( //
				id.ofType(ID).launches(app).build("ID No.", "id"), //
				customer.ofType(TEXT).launches(app).width(240).build("Partner", "customer"), //
				checkDate.ofType(DATE).launches(app).build("Check Date", "checkDate"), //
				bank.ofType(TEXT).launches(app).width(240).build("Bank", "bank"), //
				checkId.ofType(ID).launches(app).build("Check No.", "checkId"), //
				totalValue.ofType(CURRENCY).launches(app).build("Amount", "totalValue"), //
				balanceValue.ofType(CURRENCY).launches(app).build("Balance", "balanceValue"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
	}
}
