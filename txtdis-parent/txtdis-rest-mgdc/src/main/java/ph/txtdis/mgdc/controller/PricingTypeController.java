package ph.txtdis.mgdc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dto.PricingType;
import ph.txtdis.mgdc.service.server.PricingTypeService;

@RequestMapping("/pricingTypes")
@RestController("pricingTypeController")
public class PricingTypeController
	extends AbstractNameListController<PricingTypeService, PricingType> {
}