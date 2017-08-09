package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.PricingType;
import ph.txtdis.mgdc.domain.PricingTypeEntity;
import ph.txtdis.service.SavedNameListService;

public interface PricingTypeService
	extends SavedNameListService<PricingType> {

	PricingTypeEntity findEntityByName(String text);

	PricingType toModel(PricingTypeEntity type);

	PricingTypeEntity toEntity(PricingType type);
}