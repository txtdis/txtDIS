package ph.txtdis.dyvek.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.dyvek.service.server.ItemService;

@RequestMapping("/items")
@RestController("itemController")
public class ItemControllerImpl //
	extends AbstractNameListController<ItemService, Item> {
}