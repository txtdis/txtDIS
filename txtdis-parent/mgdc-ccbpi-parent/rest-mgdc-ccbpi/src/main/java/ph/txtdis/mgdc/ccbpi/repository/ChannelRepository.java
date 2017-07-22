package ph.txtdis.mgdc.ccbpi.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.ChannelEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("channelRepository")
public interface ChannelRepository //
		extends NameListRepository<ChannelEntity> {
}
