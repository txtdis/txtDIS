package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("blanketBalanceService")
public class BlanketBalanceServiceImpl extends AbstractSalesItemVarianceService implements BlanketBalanceService {

	@Override
	public String getHeaderText() {
		return "Blanket Order Balance";
	}

	@Override
	public String getModule() {
		return "blanketBalance";
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
