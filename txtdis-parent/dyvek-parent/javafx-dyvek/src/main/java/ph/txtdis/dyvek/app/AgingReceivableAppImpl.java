package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.table.AgingReceivableTable;
import ph.txtdis.dyvek.model.Aging;
import ph.txtdis.dyvek.service.AgingReceivableService;

@Scope("prototype")
@Component("agingReceivableApp")
public class AgingReceivableAppImpl //
		extends AbstractListApp<AgingReceivableTable, AgingReceivableService, Aging>//
		implements AgingReceivableApp {
}
