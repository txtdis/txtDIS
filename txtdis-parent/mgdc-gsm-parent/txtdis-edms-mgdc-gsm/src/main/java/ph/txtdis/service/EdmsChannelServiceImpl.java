package ph.txtdis.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsOutletType;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.repository.EdmsChannelRepository;

import static ph.txtdis.type.BillingType.INVOICE;

@Service("channelService")
public class EdmsChannelServiceImpl
	extends AbstractCreateNameListService<EdmsChannelRepository, EdmsOutletType, Channel> //
	implements EdmsChannelService {

	@Override
	protected Channel toModel(EdmsOutletType e) {
		return e == null ? null : toDTO(e.getName());
	}

	@Override
	public Channel toDTO(String name) {
		String initial = StringUtils.substringBefore(name, "-").trim();
		EdmsOutletType e = initial.isEmpty() ? null : repository.findFirstByNameStartingWithIgnoreCase(initial);
		return e == null ? null : newChannel(e.getName());
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name.toUpperCase().trim());
		c.setBillingType(INVOICE);
		c.setVisited(true);
		return c;
	}

	@Override
	protected EdmsOutletType toEntity(Channel t) {
		return null;
	}
}