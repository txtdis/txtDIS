package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.dialog.GoodRmaDialog;
import ph.txtdis.mgdc.fx.table.GoodRmaTable;
import ph.txtdis.mgdc.gsm.service.GoodRefundedRmaService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("goodRmaTable")
public class GoodRmaTableImpl //
	extends AbstractBeverageBillableTable<GoodRefundedRmaService, GoodRmaDialog> //
	implements GoodRmaTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.addAll(asList(price, bookedQtyInFractions, subtotal));
		return l;
	}

	@Override
	protected String subtotal() {
		return "initialSubtotalValue";
	}
}
