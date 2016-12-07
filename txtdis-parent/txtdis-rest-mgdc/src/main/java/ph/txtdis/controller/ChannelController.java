package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Channel;
import ph.txtdis.service.ChannelService;

@RequestMapping("/channels")
@RestController("channelController")
public class ChannelController extends AbstractNameListController<ChannelService, Channel> {

	@RequestMapping(path = "/visited", method = GET)
	public ResponseEntity<?> findVisited() {
		List<Channel> l = service.findVisited();
		return new ResponseEntity<>(l, OK);
	}
}