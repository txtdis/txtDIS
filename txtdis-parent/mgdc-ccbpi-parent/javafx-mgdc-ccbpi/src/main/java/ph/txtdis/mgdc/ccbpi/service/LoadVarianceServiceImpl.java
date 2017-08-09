package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;

@Service("loadVarianceService")
public class LoadVarianceServiceImpl //
	extends AbstractSalesItemVarianceService //
	implements LoadVarianceService {

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
		return "Load Variance";
	}

	@Override
	public String getModuleName() {
		return "loadVariance";
	}
}
