package ph.txtdis.mgdc.service;

import java.util.List;

import ph.txtdis.dto.PricingType;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.SavedByName;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

public interface PricingTypeService //
		extends ListedAndResetableService<PricingType>, //
		SavedByName<PricingType>, //
		TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, //
		UniqueNamedService<PricingType> {

	List<String> listNames();
}