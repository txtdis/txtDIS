package ph.txtdis.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Channel;

@Repository("channelRepository")
public interface ChannelRepository extends NameListRepository<Channel> {

	List<Channel> findByVisitedTrue();
}
