package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.PricingType;
import ph.txtdis.service.PricingTypeService;

@RequestMapping("/pricingTypes")
@RestController("pricingTypeController")
public class PricingTypeController extends AbstractNameListController<PricingTypeService, PricingType> {
}