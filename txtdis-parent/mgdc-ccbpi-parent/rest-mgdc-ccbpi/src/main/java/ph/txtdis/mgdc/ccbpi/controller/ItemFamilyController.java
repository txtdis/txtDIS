package ph.txtdis.mgdc.ccbpi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.mgdc.controller.AbstractItemFamilyController;
import ph.txtdis.mgdc.service.server.ItemFamilyService;

@RequestMapping("/itemFamilies")
@RestController("itemFamilyController")
public class ItemFamilyController //
	extends AbstractItemFamilyController<ItemFamilyService> {
}