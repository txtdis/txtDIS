package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.PricingType;
import ph.txtdis.repository.PricingTypeRepository;

@RestController("pricingTypeController")
@RequestMapping("/pricingTypes")
public class PricingTypeController extends NameListController<PricingTypeRepository, PricingType> {
}