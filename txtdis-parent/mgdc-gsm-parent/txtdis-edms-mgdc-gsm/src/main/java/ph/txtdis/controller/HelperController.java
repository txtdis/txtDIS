package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.User;
import ph.txtdis.service.HelperService;

@RequestMapping("/helpers")
@RestController("helperController")
public class HelperController //
		extends AbstractSavedController<HelperService, User, String> {
}