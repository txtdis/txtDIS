package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.table.AgingPayableTable;
import ph.txtdis.dyvek.model.Aging;
import ph.txtdis.dyvek.service.AgingPayableService;

@Scope("prototype")
@Component("agingPayableApp")
public class AgingPayableAppImpl
	extends AbstractListApp<AgingPayableTable, AgingPayableService, Aging>
	implements AgingPayableApp {
}
