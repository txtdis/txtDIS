package ph.txtdis.service;

import ph.txtdis.dto.Channel;

public interface ChannelService extends NameListCreateService<Channel> {

	Channel toDTO(String name);
}