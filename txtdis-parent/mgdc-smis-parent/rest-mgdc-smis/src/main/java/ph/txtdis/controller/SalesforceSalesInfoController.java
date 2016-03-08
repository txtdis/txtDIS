package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.SalesforceSalesInfo;
import ph.txtdis.service.SalesforceService;

@RequestMapping("/salesforceSalesInfos")
@RestController("salesforceSalesInfoController")
public class SalesforceSalesInfoController {

	@Autowired
	private SalesforceService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<SalesforceSalesInfo> l = service.listInvoicesForUpload();
		return new ResponseEntity<>(l, OK);
	}
}