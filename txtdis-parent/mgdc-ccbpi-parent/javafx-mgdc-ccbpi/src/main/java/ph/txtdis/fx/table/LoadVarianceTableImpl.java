package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.LoadVarianceService;

@Scope("prototype")
@Component("loadVarianceTable")
public class LoadVarianceTableImpl extends AbstractVarianceTable<LoadVarianceService> implements LoadVarianceTable {

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().remove(seller);
	}
}
