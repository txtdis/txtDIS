package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Channel;

public interface ChannelService extends NameListCreateService<Channel> {

	List<Channel> findVisited();
}