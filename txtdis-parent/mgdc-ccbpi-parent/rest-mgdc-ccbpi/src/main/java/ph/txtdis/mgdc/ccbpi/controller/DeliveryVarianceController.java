package ph.txtdis.mgdc.ccbpi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.mgdc.ccbpi.service.server.DeliveryVarianceService;

@RequestMapping("/deliveryVariances")
@RestController("deliveryVarianceController")
public class DeliveryVarianceController extends AbstractVarianceController<DeliveryVarianceService> {
}