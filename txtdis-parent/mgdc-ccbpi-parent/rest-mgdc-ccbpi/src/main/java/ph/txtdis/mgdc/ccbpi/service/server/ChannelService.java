package ph.txtdis.mgdc.ccbpi.service.server;

import ph.txtdis.mgdc.ccbpi.domain.ChannelEntity;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.service.server.ConvertibleService;
import ph.txtdis.service.SavedNameListService;

public interface ChannelService //
	extends SavedNameListService<Channel>,
	ConvertibleService<ChannelEntity, Channel> {
}