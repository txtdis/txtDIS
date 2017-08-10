package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.service.LoadVarianceService;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("loadVarianceTable")
public class LoadVarianceTableImpl //
	extends AbstractVarianceTable<LoadVarianceService> //
	implements LoadVarianceTable {

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> addColumns() {
		List<TableColumn<SalesItemVariance, ?>> l = new ArrayList<>(super.addColumns());
		l.remove(seller);
		return l;
	}
}
