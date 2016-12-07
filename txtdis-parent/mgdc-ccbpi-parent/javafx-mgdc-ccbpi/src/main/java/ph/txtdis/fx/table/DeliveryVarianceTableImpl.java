package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.DeliveryVarianceService;

@Scope("prototype")
@Component("deliveryVarianceTable")
public class DeliveryVarianceTableImpl extends AbstractVarianceTable<DeliveryVarianceService>
		implements DeliveryVarianceTable {

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().remove(seller);
	}
}
