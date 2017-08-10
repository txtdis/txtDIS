package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.SalesItemVariance;

import java.util.List;

import static java.util.Collections.emptyList;

@Service("blanketBalanceService")
public class BlanketBalanceServiceImpl //
	extends AbstractSalesItemVarianceService //
	implements BlanketBalanceService {

	@Override
	public String getActualColumnName() {
		return "Delivered";
	}

	@Override
	public String getExpectedColumnName() {
		return "Booked";
	}

	@Override
	public String getHeaderName() {
		return "Blanket Order Balance";
	}

	@Override
	public String getReturnedColumnName() {
		return "Returned";
	}

	@Override
	public String getVarianceColumnName() {
		return "Balance";
	}

	@Override
	public List<SalesItemVariance> list() {
		try {
			return getRestClientServiceForLists().module(getModuleName()).getList();
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public String getModuleName() {
		return "blanketBalance";
	}
}
