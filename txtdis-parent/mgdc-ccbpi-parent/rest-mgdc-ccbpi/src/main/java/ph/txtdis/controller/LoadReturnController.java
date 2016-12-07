package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.PickList;
import ph.txtdis.service.LoadReturnService;

@RequestMapping("/loadReturns")
@RestController("loadReturnController")
public class LoadReturnController extends AbstractSpunController<LoadReturnService, PickList, Long> {
}