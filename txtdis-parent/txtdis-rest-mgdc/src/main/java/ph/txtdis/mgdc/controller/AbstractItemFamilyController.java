package ph.txtdis.mgdc.controller;

import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.mgdc.service.server.ItemFamilyService;

public abstract class AbstractItemFamilyController<S extends ItemFamilyService> //
		extends AbstractNameListController<S, ItemFamily> {
}