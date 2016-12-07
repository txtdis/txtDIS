package ph.txtdis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Channel;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("channelService")
public class ChannelServiceImpl extends AbstractChannelService implements ImportedChannelService {

	@Autowired
	private ReadOnlyService<Channel> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		List<Channel> l = new ArrayList<>(readOnlyService.module("channel").getList());
		l.add(newChannel("OTHERS"));
		repository.save(toEntities(l));
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name);
		return c;
	}
}