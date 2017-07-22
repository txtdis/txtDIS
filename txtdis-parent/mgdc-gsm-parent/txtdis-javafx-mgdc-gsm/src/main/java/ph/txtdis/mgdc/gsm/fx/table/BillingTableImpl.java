package ph.txtdis.mgdc.gsm.fx.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.table.BeverageBillingTable;
import ph.txtdis.mgdc.gsm.fx.dialog.BillingDialog;
import ph.txtdis.mgdc.gsm.service.BillingService;

@Scope("prototype")
@Component("billingTable")
public class BillingTableImpl //
		extends AbstractBeverageBillableTable<BillingService, BillingDialog> //
		implements BeverageBillingTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.addAll(Arrays.asList(price, qtyColumn(), subtotal));
		return l;
	}
}
