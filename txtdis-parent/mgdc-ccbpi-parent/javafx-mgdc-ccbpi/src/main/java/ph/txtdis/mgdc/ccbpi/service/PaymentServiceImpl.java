package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;

@Service("paymentService")
public class PaymentServiceImpl //
		extends AbstractSalesItemVarianceService //
		implements PaymentService {

	@Override
	public String getExpectedColumnName() {
		return "Received";
	}

	@Override
	public String getHeaderName() {
		return "Delivery Payment";
	}

	@Override
	public String getModuleName() {
		return "payment";
	}

	@Override
	public String getReturnedColumnName() {
		return "Returned";
	}

	@Override
	public String getVarianceColumnName() {
		return "Net";
	}
}
