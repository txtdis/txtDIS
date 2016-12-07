package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.ChannelEntity;
import ph.txtdis.dto.Channel;
import ph.txtdis.repository.ChannelRepository;

public abstract class AbstractChannelService extends AbstractCreateNameListService<ChannelRepository, ChannelEntity, Channel>
		implements PrimaryChannelService {

	@Override
	public List<Channel> findVisited() {
		List<ChannelEntity> l = repository.findByVisitedTrue();
		return toList(l);
	}

	@Override
	public boolean areEqual(ChannelEntity e, Channel c) {
		if (e == null)
			return c == null;
		if (c == null)
			return false;
		return e.getName().equals(c.getName());
	}

	@Override
	public Channel toDTO(ChannelEntity e) {
		return e == null ? null : newChannel(e);
	}

	private Channel newChannel(ChannelEntity e) {
		Channel c = new Channel();
		c.setId(e.getId());
		c.setName(e.getName());
		c.setBillingType(e.getBillingType());
		c.setVisited(e.isVisited());
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
		e.setBillingType(c.getBillingType());
		e.setVisited(c.isVisited());
		return e;
	}
}