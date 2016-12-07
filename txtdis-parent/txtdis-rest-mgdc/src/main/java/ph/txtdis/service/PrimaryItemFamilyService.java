package ph.txtdis.service;

import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.dto.ItemFamily;

public interface PrimaryItemFamilyService extends ItemFamilyService {

	ItemFamilyEntity toEntity(ItemFamily family);

	ItemFamily toDTO(ItemFamilyEntity family);
}