package ph.txtdis.mgdc.service;

import ph.txtdis.dto.PricingType;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.SavedByName;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

import java.util.List;

public interface PricingTypeService //
	extends ListedAndResettableService<PricingType>, //
	SavedByName<PricingType>, //
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, //
	UniqueNamedService<PricingType> {

	List<String> listNames();
}