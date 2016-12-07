package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.ItemService;

@RequestMapping("/items")
@RestController("itemController")
public class ItemControllerImpl extends AbstractItemController<ItemService> {
}