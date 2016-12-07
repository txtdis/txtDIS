package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsOutletType;
import ph.txtdis.dto.Channel;
import ph.txtdis.repository.EdmsChannelRepository;
import ph.txtdis.type.BillingType;

@Service("channelService")
public class ChannelServiceImpl extends AbstractCreateNameListService<EdmsChannelRepository, EdmsOutletType, Channel>
		implements ChannelService {

	private static Logger logger = getLogger(ChannelServiceImpl.class);

	@Override
	public Channel toDTO(String name) {
		String initial = StringUtils.substringBefore(name, "-").trim();
		EdmsOutletType e = initial.isEmpty() ? null : repository.findByNameStartingWithIgnoreCase(initial);
		return e == null ? null : newChannel(e.getName());
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name.toUpperCase().trim());
		c.setBillingType(BillingType.INVOICE);
		c.setVisited(true);
		logger.info("\n    Channel: " + c.getName());
		return c;
	}

	@Override
	protected Channel toDTO(EdmsOutletType e) {
		return toDTO(e.getName());
	}

	@Override
	protected EdmsOutletType toEntity(Channel t) {
		// TODO Auto-generated method stub
		return null;
	}
}