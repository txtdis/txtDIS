package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.service.DeliveryVarianceService;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("deliveryVarianceTable")
public class DeliveryVarianceTableImpl
	extends AbstractVarianceTable<DeliveryVarianceService>
	implements DeliveryVarianceTable {

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> addColumns() {
		List<TableColumn<SalesItemVariance, ?>> l = new ArrayList<>(super.addColumns());
		l.remove(seller);
		return l;
	}
}
