package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.DeliveryVarianceTable;
import ph.txtdis.service.DeliveryVarianceService;

@Scope("prototype")
@Component("deliveryVarianceApp")
public class DeliveryVarianceAppImpl
		extends AbstractReportApp<DeliveryVarianceTable, DeliveryVarianceService, SalesItemVariance>
		implements DeliveryVarianceApp {
}
