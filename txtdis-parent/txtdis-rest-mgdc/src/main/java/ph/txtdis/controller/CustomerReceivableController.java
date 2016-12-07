package ph.txtdis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.service.CustomerReceivableService;

@RequestMapping("/customerReceivables")
@RestController("customerReceivableController")
public class CustomerReceivableController {

	@Autowired
	private CustomerReceivableService service;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> customerReceivableReport(@RequestParam("customer") Long customerId,
			@RequestParam("lowerDayCount") long low, @RequestParam("upperDayCount") long up) {
		CustomerReceivableReport r = service.generateCustomerReceivableReport(customerId, low, up);
		return new ResponseEntity<>(r, HttpStatus.OK);
	}
}