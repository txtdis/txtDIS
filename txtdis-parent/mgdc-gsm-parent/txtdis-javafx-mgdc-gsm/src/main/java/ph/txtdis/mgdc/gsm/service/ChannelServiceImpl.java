package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.BillingType;
import ph.txtdis.util.ClientTypeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ph.txtdis.util.UserUtils.username;

@Service("channelService")
public class ChannelServiceImpl //
	implements ChannelService {

	@Autowired
	private RestClientService<Channel> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public RestClientService<Channel> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<Channel> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + ChannelService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<Channel> listAllChannels() throws Exception {
		List<Channel> l = new ArrayList<>();
		l.add(getChannelForAll());
		l.addAll(list());
		return l;
	}

	@Override
	public Channel getChannelForAll() {
		return newChannel("ALL");
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name);
		return c;
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(u -> u.getName()).sorted().collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Channel> listVisitedChannels() {
		try {
			return restClientService.module(getModuleName()).getList("/visited");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "channel";
	}

	@Override
	public Channel nullIfAll(Channel f) {
		if (f == null)
			return null;
		return f.equals(getChannelForAll()) ? null : f;
	}

	@Override
	public void reset() {
	}

	@Override
	public Channel save(String name, BillingType type, boolean visited) throws Information, Exception {
		Channel c = newChannel(name);
		c.setBillingType(type);
		c.setVisited(visited);
		return restClientService.module(getModuleName()).save(c);
	}
}
