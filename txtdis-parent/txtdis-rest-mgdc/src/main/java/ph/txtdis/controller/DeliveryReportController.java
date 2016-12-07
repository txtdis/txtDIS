package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.DeliveryReportService;

@RequestMapping("/deliveryReports")
@RestController("deliveryReportController")
public class DeliveryReportController extends AbstractBillableController<DeliveryReportService> {
}