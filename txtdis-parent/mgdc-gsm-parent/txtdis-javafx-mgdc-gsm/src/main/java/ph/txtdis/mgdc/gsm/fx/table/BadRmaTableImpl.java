package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.dialog.BadRmaDialog;
import ph.txtdis.mgdc.fx.table.BadRmaTable;
import ph.txtdis.mgdc.gsm.service.BadRmaService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("badRmaTable")
public class BadRmaTableImpl
	extends AbstractBeverageBillableTable<BadRmaService, BadRmaDialog>
	implements BadRmaTable {

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
