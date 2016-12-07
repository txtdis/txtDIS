package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.ReceivingReportService;

@RequestMapping("/receivingReports")
@RestController("receivingReportController")
public class ReceivingReportController extends AbstractBillableController<ReceivingReportService> {
}