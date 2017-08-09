package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.domain.ChannelEntity;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.service.server.ConvertibleService;
import ph.txtdis.service.SavedNameListService;

import java.util.List;

public interface ChannelService //
	extends SavedNameListService<Channel>,
	ConvertibleService<ChannelEntity, Channel> {

	List<Channel> findVisited();
}