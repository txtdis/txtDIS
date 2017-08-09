package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.ReceivingReportService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/receivingReports")
@RestController("receivingReportController")
public class ReceivingReportController //
	extends AbstractSpunSavedReferencedKeyedController<ReceivingReportService, BillableEntity, Billable> {

	@RequestMapping(path = "/booking", method = GET)
	public ResponseEntity<?> booking(@RequestParam("id") Long id) throws NotFoundException {
		Billable b = service.findByBookingId(id);
		return new ResponseEntity<>(b, OK);
	}
}