package ph.txtdis.mgdc.ccbpi.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.BadRmaService;
import ph.txtdis.mgdc.fx.dialog.BadRmaDialog;
import ph.txtdis.mgdc.fx.table.BadRmaTable;

@Scope("prototype")
@Component("badRmaTable")
public class BadRmaTableImpl //
		extends AbstractBeverageBillableTable<BadRmaService, BadRmaDialog> //
		implements BadRmaTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.add(bookedQtyInFractions);
		return l;
	}
}
