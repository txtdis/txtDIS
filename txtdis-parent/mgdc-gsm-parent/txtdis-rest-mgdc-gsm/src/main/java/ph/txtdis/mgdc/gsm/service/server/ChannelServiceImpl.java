package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.domain.ChannelEntity;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.repository.ChannelRepository;
import ph.txtdis.service.AbstractCreateNameListService;
import ph.txtdis.service.RestClientService;

import java.util.ArrayList;
import java.util.List;

@Service("channelService")
public class ChannelServiceImpl //
	extends AbstractCreateNameListService<ChannelRepository, ChannelEntity, Channel> //
	implements ImportedChannelService {

	@Autowired
	private RestClientService<Channel> restClientService;

	@Override
	public List<Channel> findVisited() {
		List<ChannelEntity> l = repository.findByVisitedTrue();
		return toModels(l);
	}

	@Override
	public void importAll() throws Exception {
		List<Channel> l = new ArrayList<>(restClientService.module("channel").getList());
		l.add(newChannel("OTHERS"));
		repository.save(toEntities(l));
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name);
		return c;
	}

	@Override
	public Channel toModel(ChannelEntity e) {
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