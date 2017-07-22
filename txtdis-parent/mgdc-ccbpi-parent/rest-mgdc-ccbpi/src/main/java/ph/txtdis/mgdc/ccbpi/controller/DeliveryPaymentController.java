package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.service.server.DeliveryPaymentService;

@RequestMapping("/payments")
@RestController("paymentController")
public class DeliveryPaymentController {

	@Autowired
	private DeliveryPaymentService service;

	@RequestMapping(path = "/ddp", method = GET)
	public ResponseEntity<?> find(@RequestParam("date") Date d) {
		List<SalesItemVariance> l = service.list(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}