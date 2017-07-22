package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.service.EdmsChannelService;

@RequestMapping("/channels")
@RestController("channelController")
public class ChannelController extends AbstractNameListController<EdmsChannelService, Channel> {
}