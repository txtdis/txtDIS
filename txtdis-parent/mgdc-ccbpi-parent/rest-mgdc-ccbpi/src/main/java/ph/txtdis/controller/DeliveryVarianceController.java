package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.DeliveryVarianceService;

@RequestMapping("/deliveryVariances")
@RestController("deliveryVarianceController")
public class DeliveryVarianceController extends AbstractVarianceController<DeliveryVarianceService> {
}