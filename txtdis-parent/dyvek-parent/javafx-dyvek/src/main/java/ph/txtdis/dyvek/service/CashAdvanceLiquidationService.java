package ph.txtdis.dyvek.service;

import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface CashAdvanceLiquidationService //
		extends ListedAndResetableService<RemittanceDetail>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	void openByDoubleClickedTableCellKey(String key) throws Exception;
}
