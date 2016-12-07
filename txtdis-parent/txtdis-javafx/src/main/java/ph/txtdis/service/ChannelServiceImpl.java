package ph.txtdis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Channel;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.BillingType;
import ph.txtdis.util.ClientTypeMap;

@Service("channelService")
public class ChannelServiceImpl implements ChannelService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Channel> readOnlyService;

	@Autowired
	private SavingService<Channel> savingService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ReadOnlyService<Channel> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModule() {
		return "channel";
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + ChannelService.super.getTitleText();
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
	public List<Channel> listVisitedChannels() {
		try {
			return readOnlyService.module(getModule()).getList("/visited");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Channel save(String name, BillingType type, boolean visited)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Channel c = newChannel(name);
		c.setBillingType(type);
		c.setVisited(visited);
		return savingService.module(getModule()).save(c);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public Channel getChannelForAll() {
		return newChannel("ALL");
	}

	protected Channel newChannel(String name) {
		Channel c = new Channel();
		c.setName(name);
		return c;
	}

	@Override
	public List<Channel> listAllChannels() throws Exception {
		List<Channel> l = new ArrayList<>();
		l.add(getChannelForAll());
		l.addAll(list());
		return l;
	}

	@Override
	public Channel nullIfAll(Channel f) {
		if (f == null)
			return null;
		return f.equals(getChannelForAll()) ? null : f;
	}
}
