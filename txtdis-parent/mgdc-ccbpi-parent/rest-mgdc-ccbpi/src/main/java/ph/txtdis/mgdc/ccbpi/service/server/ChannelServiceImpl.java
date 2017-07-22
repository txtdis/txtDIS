package ph.txtdis.mgdc.ccbpi.service.server;

import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.ccbpi.domain.ChannelEntity;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.repository.ChannelRepository;
import ph.txtdis.service.AbstractCreateNameListService;

@Service("channelService")
public class ChannelServiceImpl //
		extends AbstractCreateNameListService<ChannelRepository, ChannelEntity, Channel> //
		implements ChannelService {

	@Override
	public Channel toModel(ChannelEntity e) {
		return e == null ? null : newChannel(e);
	}

	private Channel newChannel(ChannelEntity e) {
		Channel c = new Channel();
		c.setId(e.getId());
		c.setName(e.getName());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		return c;
	}

	@Override
	public ChannelEntity toEntity(Channel c) {
		return c == null ? null : getEntity(c);
	}

	private ChannelEntity getEntity(Channel c) {
		ChannelEntity e = repository.findByNameIgnoreCase(c.getName());
		return e != null ? e : newEntity(c);
	}

	private ChannelEntity newEntity(Channel c) {
		ChannelEntity e = new ChannelEntity();
		e.setName(c.getName());
		return e;
	}
}