package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.PurchaseOrderService;

@RequestMapping("/purchaseOrders")
@RestController("purchaseOrderController")
public class PurchaseOrderController extends AbstractBillableController<PurchaseOrderService> {
}