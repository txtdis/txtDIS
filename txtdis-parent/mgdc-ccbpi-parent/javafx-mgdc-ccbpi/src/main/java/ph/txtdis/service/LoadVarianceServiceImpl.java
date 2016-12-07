package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("loadVarianceService")
public class LoadVarianceServiceImpl extends AbstractSalesItemVarianceService implements LoadVarianceService {

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
		return "Load Variance";
	}

	@Override
	public String getModule() {
		return "loadVariance";
	}
}
