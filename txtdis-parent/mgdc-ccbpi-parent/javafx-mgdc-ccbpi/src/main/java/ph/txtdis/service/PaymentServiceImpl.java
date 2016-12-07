package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("paymentService")
public class PaymentServiceImpl extends AbstractSalesItemVarianceService implements PaymentService {

	@Override
	public String getHeaderText() {
		return "Delivery Payment";
	}

	@Override
	public String getModule() {
		return "payment";
	}

	@Override
	public String getActualHeader() {
		return "DDL";
	}

	@Override
	public String getExpectedHeader() {
		return "OCS";
	}
}
