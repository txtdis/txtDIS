package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.PaymentTable;
import ph.txtdis.service.PaymentService;

@Scope("prototype")
@Component("paymentApp")
public class PaymentAppImpl extends AbstractTotaledReportApp<PaymentTable, PaymentService, SalesItemVariance>
		implements PaymentApp {

	@Override
	protected int noOfTotalDisplays() {
		return 4;
	}
}
