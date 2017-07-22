package ph.txtdis.dyvek.fx.table;

import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ENUM;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.fx.table.Column;

@Scope("prototype")
@Component("purchaseListTable")
public class PurchaseListTableImpl //
		extends AbstractOrderListTable //
		implements PurchaseListTable {

	@Autowired
	private Column<Billable, LocalDate> endDate;

	@Autowired
	private Column<Billable, String> status;

	private boolean noStatus;

	@Override
	protected List<TableColumn<Billable, ?>> addColumns() {
		List<TableColumn<Billable, ?>> l = new ArrayList<>(super.addColumns());
		l.add(1, endDate.ofType(DATE).launches(app).build("Valid till", "endDate"));
		if (!noStatus)
			l.add(status.ofType(ENUM).launches(app).width(80).build("Status", "status"));
		return l;
	}

	@Override
	protected String customerColumnName() {
		return "Supplier";
	}

	@Override
	protected String customerGetterName() {
		return "vendor";
	}

	@Override
	protected String dateColumnName() {
		return "Order";
	}

	@Override
	public PurchaseListTable noStatusColumn() {
		noStatus = true;
		return this;
	}

	@Override
	protected String orderNoColumnName() {
		return "P/O No";
	}

	@Override
	protected String orderNoGetterName() {
		return "purchaseNo";
	}

	@Override
	protected String qtyColumnName() {
		return "Balance";
	}

	@Override
	protected String qtyGetterName() {
		return "balanceQty";
	}

}
