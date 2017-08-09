package ph.txtdis.mgdc.ccbpi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.PickList;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.service.server.LoadReturnService;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;

@RequestMapping("/loadReturns")
@RestController("loadReturnController")
public class LoadReturnController //
	extends AbstractSpunSavedReferencedKeyedController<LoadReturnService, PickListEntity, PickList> {
}