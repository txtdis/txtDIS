package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.SalesOrderService;

@RequestMapping("/salesOrders")
@RestController("salesOrderController")
public class SalesOrderController extends AbstractBillableController<SalesOrderService> {
}