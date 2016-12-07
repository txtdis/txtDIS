package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("deliveryVarianceService")
public class DeliveryVarianceServiceImpl extends AbstractSalesItemVarianceService implements DeliveryVarianceService {

	@Override
	public String getActualHeader() {
		return "DDL";
	}

	@Override
	public String getExpectedHeader() {
		return "L/M";
	}

	@Override
	public String getHeaderText() {
		return "Delivery Variance";
	}

	@Override
	public String getModule() {
		return "deliveryVariance";
	}
}
