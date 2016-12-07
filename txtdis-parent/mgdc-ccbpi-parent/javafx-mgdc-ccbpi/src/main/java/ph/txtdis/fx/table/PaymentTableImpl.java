package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.PaymentService;

@Scope("prototype")
@Component("paymentTable")
public class PaymentTableImpl extends AbstractVarianceTable<PaymentService> implements PaymentTable {

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}
}
