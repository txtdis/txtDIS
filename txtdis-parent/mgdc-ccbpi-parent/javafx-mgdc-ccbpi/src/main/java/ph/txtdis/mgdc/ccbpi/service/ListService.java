package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.service.Spreadsheet;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.TotaledService;

public interface ListService //
	extends Spreadsheet<SalesItemVariance>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	TotaledService<SalesItemVariance> {
}
