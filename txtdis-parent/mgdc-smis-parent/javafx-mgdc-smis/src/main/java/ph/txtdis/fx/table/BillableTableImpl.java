package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.BillableDialog;
import ph.txtdis.service.BillableService;

@Scope("prototype")
@Component("billableTable")
public class BillableTableImpl extends AbstractBillableTable<BillableService, BillableDialog> {

	@Autowired
	private Column<BillableDetail, BigDecimal> qtyInDecimals;

	@Override
	public void initializeColumns() {
		super.initializeColumns();
		qtyInDecimals.ofType(QUANTITY).build("Quantity", qty());
	}

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return qtyInDecimals;
	}
}
