package ph.txtdis.dyvek.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.VendorBillService;

@RequestMapping("/vendorBills")
@RestController("vendorBillController")
public class VendorBillController //
		extends AbstractOpenListedSearchedSpunSavedController<VendorBillService, BillableEntity, Billable> {
}