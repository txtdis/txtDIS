package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;
import ph.txtdis.type.BillingType;

public interface ChannelService //
		extends ListedAndResetableService<Channel>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, UniqueNamedService<Channel> {

	Channel getChannelForAll();

	List<Channel> listAllChannels() throws Exception;

	List<String> listNames();

	List<Channel> listVisitedChannels();

	Channel nullIfAll(Channel f);

	Channel save(String name, BillingType type, boolean visited) throws Information, Exception;
}