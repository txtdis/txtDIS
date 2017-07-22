package ph.txtdis.mgdc.gsm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.type.BillingType;
import ph.txtdis.util.ClientTypeMap;

@Service("channelService")
public class ChannelServiceImpl //
		implements ChannelService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Channel> readOnlyService;

	@Autowired
	private SavingService<Channel> savingService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public Channel getChannelForAll() {
		return newChannel("ALL");
	}

	@Override
	public ReadOnlyService<Channel> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "channel";
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + ChannelService.super.getTitleName();
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
			return readOnlyService.module(getModuleName()).getList("/visited");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		return savingService.module(getModuleName()).save(c);
	}

	private Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name);
		return c;
	}
}
