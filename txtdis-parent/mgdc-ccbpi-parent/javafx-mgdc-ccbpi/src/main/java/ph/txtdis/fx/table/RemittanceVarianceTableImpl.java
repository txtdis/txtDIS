package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.RemittanceVarianceService;

@Scope("prototype")
@Component("remittanceVarianceTable")
public class RemittanceVarianceTableImpl extends AbstractVarianceTable<RemittanceVarianceService>
		implements RemittanceVarianceTable {

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().remove(seller);
	}
}
