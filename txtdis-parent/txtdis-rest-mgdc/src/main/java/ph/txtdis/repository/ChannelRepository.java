package ph.txtdis.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ChannelEntity;

@Repository("channelRepository")
public interface ChannelRepository extends NameListRepository<ChannelEntity> {

	List<ChannelEntity> findByVisitedTrue();
}
