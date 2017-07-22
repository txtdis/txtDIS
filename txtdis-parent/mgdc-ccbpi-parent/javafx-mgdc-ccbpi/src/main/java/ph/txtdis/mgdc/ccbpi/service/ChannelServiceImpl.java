package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
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
		return savingService.module(getModuleName()).save(c);
	}
}
