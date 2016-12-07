package ph.txtdis.service;

import ph.txtdis.domain.ChannelEntity;
import ph.txtdis.dto.Channel;

public interface PrimaryChannelService extends ChannelService {

	ChannelEntity toEntity(Channel limit);

	Channel toDTO(ChannelEntity limit);

	boolean areEqual(ChannelEntity entityLimit, Channel limit);
}