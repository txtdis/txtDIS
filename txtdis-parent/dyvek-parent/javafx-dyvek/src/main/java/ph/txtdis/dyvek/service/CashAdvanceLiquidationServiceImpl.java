package ph.txtdis.dyvek.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.util.ClientTypeMap;

@Service("cashAdvanceLiquidationService")
public class CashAdvanceLiquidationServiceImpl //
		implements CashAdvanceLiquidationService {

	@Autowired
	private ReadOnlyService<RemittanceDetail> readOnlyService;

	@Autowired
	private ClientTypeMap typeMap;

	private List<RemittanceDetail> list;

	@Override
	public ReadOnlyService<RemittanceDetail> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "cashAdvanceLiquidation";
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
		list = getListedReadOnlyService().module(getModuleName()).getList("/find?id=" + key);
	}

	@Override
	public void reset() {
		list = null;
	}
}
