package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.service.server.ChannelService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/channels")
@RestController("channelController")
public class ChannelController
	extends AbstractNameListController<ChannelService, Channel> {

	@RequestMapping(path = "/visited", method = GET)
	public ResponseEntity<?> visited() {
		List<Channel> l = service.findVisited();
		return new ResponseEntity<>(l, OK);
	}
}