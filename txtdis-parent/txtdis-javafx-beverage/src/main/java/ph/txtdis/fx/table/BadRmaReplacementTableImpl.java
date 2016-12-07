package ph.txtdis.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.BadRmaReplacementDialog;
import ph.txtdis.service.BadRmaReplacementService;

@Scope("prototype")
@Component("badRmaReplacementTable")
public class BadRmaReplacementTableImpl
		extends AbstractBeverageBillableTable<BadRmaReplacementService, BadRmaReplacementDialog>
		implements BadRmaReplacementTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.columns());
		l.add(qtyColumn());
		return l;
	}
}
