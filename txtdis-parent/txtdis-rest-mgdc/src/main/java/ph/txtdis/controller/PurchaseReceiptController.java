package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.PurchaseReceiptService;

@RequestMapping("/purchaseReceipts")
@RestController("purchaseReceiptController")
public class PurchaseReceiptController extends AbstractBillableController<PurchaseReceiptService> {
}