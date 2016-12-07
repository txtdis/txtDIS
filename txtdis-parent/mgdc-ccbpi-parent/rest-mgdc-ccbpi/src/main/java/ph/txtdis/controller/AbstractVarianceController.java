package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.service.VarianceService;

public abstract class AbstractVarianceController<S extends VarianceService> {

	@Autowired
	private S service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("start") Date s, @RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listByDate(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}