package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Channel;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.BillingType;
import ph.txtdis.util.TypeMap;

@Service
public class ChannelService implements Listed<Channel>, UniquelyNamed<Channel> {

	@Autowired
	private ReadOnlyService<Channel> readOnlyService;

	@Autowired
	private SavingService<Channel> savingService;

	@Autowired
	private ServerService serverService;

	@Autowired
	private TypeMap typeMap;

	@Override
	public String getModule() {
		return "channel";
	}

	@Override
	public ReadOnlyService<Channel> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public List<Channel> listVisitedChannels() {
		try {
			return readOnlyService.module(getModule()).getList("/visited");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Channel save(String name, BillingType type, boolean visited)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Channel entity = new Channel();
		entity.setName(name);
		entity.setType(type);
		entity.setVisited(visited);
		return savingService.module(getModule()).save(entity);
	}

	public boolean isOffSite() {
		return serverService.isOffSite();
	}
}
