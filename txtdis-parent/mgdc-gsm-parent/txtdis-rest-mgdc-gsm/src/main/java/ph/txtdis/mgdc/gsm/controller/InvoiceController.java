package ph.txtdis.mgdc.gsm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.InvoiceService;

@RequestMapping("/invoices")
@RestController("invoiceController")
public class InvoiceController // 
		extends AbstractSpunSavedReferencedKeyedController<InvoiceService, BillableEntity, Billable> {
}