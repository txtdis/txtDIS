package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Channel;
import ph.txtdis.service.ChannelService;

@RequestMapping("/channels")
@RestController("channelController")
public class ChannelController extends AbstractNameListController<ChannelService, Channel> {
}