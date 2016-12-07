package ph.txtdis.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.GoodRmaReplacementDialog;
import ph.txtdis.service.GoodRmaReplacementService;

@Scope("prototype")
@Component("goodRmaReplacementTable")
public class GoodRmaReplacementTableImpl
		extends AbstractBeverageBillableTable<GoodRmaReplacementService, GoodRmaReplacementDialog>
		implements GoodRmaReplacementTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.columns());
		l.add(qtyColumn());
		return l;
	}
}
