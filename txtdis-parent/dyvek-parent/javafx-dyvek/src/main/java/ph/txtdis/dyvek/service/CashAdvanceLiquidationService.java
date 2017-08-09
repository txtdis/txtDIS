package ph.txtdis.dyvek.service;

import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface CashAdvanceLiquidationService //
	extends ListedAndResettableService<RemittanceDetail>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	void openByDoubleClickedTableCellKey(String key) throws Exception;
}
