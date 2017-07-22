package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.ChannelEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("channelRepository")
public interface ChannelRepository //
		extends NameListRepository<ChannelEntity> {

	List<ChannelEntity> findByVisitedTrue();
}
