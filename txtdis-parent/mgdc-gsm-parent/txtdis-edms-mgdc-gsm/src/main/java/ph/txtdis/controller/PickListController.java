package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.PickList;
import ph.txtdis.service.EdmsPickListService;

@RequestMapping("/pickLists")
@RestController("pickListController")
public class PickListController //
		extends AbstractSavedController<EdmsPickListService, PickList, Long> {
}