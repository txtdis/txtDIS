package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.fx.table.PaymentVarianceTable;
import ph.txtdis.mgdc.ccbpi.service.PaymentService;

@Scope("prototype")
@Component("paymentApp")
public class PaymentAppImpl //
	extends AbstractTotaledReportApp<PaymentVarianceTable, PaymentService, SalesItemVariance> //
	implements PaymentApp {

	@Override
	protected int noOfTotalDisplays() {
		return 4;
	}
}
