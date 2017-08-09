package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.User;
import ph.txtdis.service.DriverService;

@RequestMapping("/drivers")
@RestController("driverController")
public class DriverController //
	extends AbstractSavedController<DriverService, User, String> {
}