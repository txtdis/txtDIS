package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;

@Scope("prototype")
@Component("cashAdvanceListTable")
public class CashAdvanceListTableImpl //
	extends AbstractTable<CashAdvance> //
	implements CashAdvanceListTable {

	@Autowired
	private Column<CashAdvance, BigDecimal> balanceValue;

	@Autowired
	private Column<CashAdvance, LocalDate> checkDate;

	@Autowired
	private DialogClosingOnlyApp app;

	@Override
	protected List<TableColumn<CashAdvance, ?>> addColumns() {
		return asList( //
			checkDate.ofType(DATE).launches(app).build("Check Date", "checkDate"), //
			balanceValue.ofType(CURRENCY).launches(app).build("Balance", "balanceValue"));
	}
}
