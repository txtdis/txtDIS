package ph.txtdis.mgdc.ccbpi.service;

import static java.util.Collections.emptyList;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;

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
	public String getModuleName() {
		return "blanketBalance";
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
			return getListedReadOnlyService().module(getModuleName()).getList();
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}
}
