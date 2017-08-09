package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

@Service("cashAdvanceLiquidationService")
public class CashAdvanceLiquidationServiceImpl //
	implements CashAdvanceLiquidationService {

	@Autowired
	private RestClientService<RemittanceDetail> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	private List<RemittanceDetail> list;

	@Override
	public RestClientService<RemittanceDetail> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return getHeaderName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<RemittanceDetail> list() {
		return list;
	}

	@Override
	public void openByDoubleClickedTableCellKey(String key) throws Exception {
		list = restClientService.module(getModuleName()).getList("/find?id=" + key);
	}

	@Override
	public String getModuleName() {
		return "cashAdvanceLiquidation";
	}

	@Override
	public void reset() {
		list = null;
	}
}
