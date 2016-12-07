package ph.txtdis.service;

import ph.txtdis.dto.ItemFamily;

public interface EdmsItemFamilyService extends ItemFamilyService {

	ItemFamily toClass(String name);

	ItemFamily toBrand(String name);
}
