package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.ClientBillService;
import ph.txtdis.exception.NotFoundException;

@RequestMapping("/clientBills")
@RestController("clientBillController")
public class ClientBillController //
		extends AbstractOpenListedSearchedSpunSavedController<ClientBillService, BillableEntity, Billable> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("id") String key) throws NotFoundException {
		try {
			Billable t = service.findUnbilledById(Long.valueOf(key));
			return new ResponseEntity<>(t, OK);
		} catch (NumberFormatException e) {
			throw new NotFoundException("ID No. " + key);
		}
	}

	@RequestMapping(path = "/bill", method = GET)
	public ResponseEntity<?> bill(@RequestParam("no") String no) {
		Billable t = service.findByBillId(no);
		return new ResponseEntity<>(t, OK);
	}
}