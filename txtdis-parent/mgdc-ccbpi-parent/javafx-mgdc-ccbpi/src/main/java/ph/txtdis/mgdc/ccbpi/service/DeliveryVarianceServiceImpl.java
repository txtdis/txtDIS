package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;

@Service("deliveryVarianceService")
public class DeliveryVarianceServiceImpl //
	extends AbstractSalesItemVarianceService //
	implements DeliveryVarianceService {

	@Override
	public String getActualColumnName() {
		return "DDL";
	}

	@Override
	public String getExpectedColumnName() {
		return "L/M";
	}

	@Override
	public String getHeaderName() {
		return "Delivery Variance";
	}

	@Override
	public String getModuleName() {
		return "deliveryVariance";
	}
}
