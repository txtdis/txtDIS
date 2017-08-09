package ph.txtdis.mgdc.gsm.repository;

import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.ChannelEntity;
import ph.txtdis.repository.NameListRepository;

import java.util.List;

@Repository("channelRepository")
public interface ChannelRepository //
	extends NameListRepository<ChannelEntity> {

	List<ChannelEntity> findByVisitedTrue();
}
