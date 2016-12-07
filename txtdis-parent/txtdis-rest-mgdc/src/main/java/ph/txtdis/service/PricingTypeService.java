package ph.txtdis.service;

import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.dto.PricingType;

public interface PricingTypeService extends NameListCreateService<PricingType> {

	PricingTypeEntity findEntityByName(String text);

	PricingType toDTO(PricingTypeEntity type);

	PricingTypeEntity toEntity(PricingType type);
}