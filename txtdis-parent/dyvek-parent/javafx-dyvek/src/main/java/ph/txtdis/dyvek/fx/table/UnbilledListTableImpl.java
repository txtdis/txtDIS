package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.fx.table.Column;

import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("unbilledListTable")
public class UnbilledListTableImpl //
	extends AbstractOrderListTable //
	implements UnbilledListTable {

	@Autowired
	private Column<Billable, String> recipient, salesNo;

	@Override
	protected List<TableColumn<Billable, ?>> addColumns() {
		List<TableColumn<Billable, ?>> l = new ArrayList<>(super.addColumns());
		l.add(2, recipient.ofType(TEXT).launches(app).build("Customer", "client"));
		l.add(4, salesNo.ofType(TEXT).launches(app).width(90).build("P/O No.", "salesNo"));
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
		return "Delivery";
	}

	@Override
	protected String orderNoColumnName() {
		return "D/R No.";
	}

	@Override
	protected String orderNoGetterName() {
		return "deliveryNo";
	}

	@Override
	protected String qtyColumnName() {
		return "Quantity";
	}

	@Override
	protected String qtyGetterName() {
		return "totalQty";
	}
}
