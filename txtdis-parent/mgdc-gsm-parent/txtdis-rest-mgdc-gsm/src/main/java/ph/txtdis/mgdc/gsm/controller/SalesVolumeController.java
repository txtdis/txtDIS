package ph.txtdis.mgdc.gsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.mgdc.service.server.SalesVolumeService;

import java.sql.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/salesVolumes")
@RestController("salesVolumeController")
public class SalesVolumeController {

	@Autowired
	private SalesVolumeService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("start") Date s, @RequestParam("end") Date e) throws Exception {
		List<SalesVolume> l = service.list(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}