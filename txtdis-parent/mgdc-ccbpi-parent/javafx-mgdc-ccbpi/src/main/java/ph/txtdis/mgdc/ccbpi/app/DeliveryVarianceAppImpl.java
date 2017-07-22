package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractReportApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.fx.table.DeliveryVarianceTable;
import ph.txtdis.mgdc.ccbpi.service.DeliveryVarianceService;

@Scope("prototype")
@Component("deliveryVarianceApp")
public class DeliveryVarianceAppImpl //
		extends AbstractReportApp<DeliveryVarianceTable, DeliveryVarianceService, SalesItemVariance> //
		implements DeliveryVarianceApp {
}
