package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.time.LocalDate;
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
	public List<String> listNames() {
		try {
			return list().stream().map(u -> u.getName()).sorted().collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public Channel save(String name, LocalDate start) throws Information, Exception {
		Channel c = new Channel();
		c.setName(name);
		c.setStartDate(start);
		return restClientService.module(getModuleName()).save(c);
	}

	@Override
	public String getModuleName() {
		return "channel";
	}
}
