package ph.txtdis.service;

import ph.txtdis.mgdc.gsm.dto.Channel;

public interface EdmsChannelService
		extends SavedNameListService<Channel> {

	Channel toDTO(String name);
}