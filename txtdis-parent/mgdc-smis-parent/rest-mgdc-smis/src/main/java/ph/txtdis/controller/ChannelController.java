package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Channel;
import ph.txtdis.repository.ChannelRepository;

@RestController("channelController")
@RequestMapping("/channels")
public class ChannelController extends NameListController<ChannelRepository, Channel> {

	@RequestMapping(path = "/visited", method = GET)
	public ResponseEntity<?> findVisited() {
		List<Channel> l = repository.findByVisitedTrue();
		return new ResponseEntity<>(l, OK);
	}
}