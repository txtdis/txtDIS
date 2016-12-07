package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.BillableService;

@RequestMapping("/billables")
@RestController("billableController")
public class BillableController extends AbstractIdController<BillableService, Billable, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Billable> l = service.list();
		return new ResponseEntity<>(l, OK);
	}
}