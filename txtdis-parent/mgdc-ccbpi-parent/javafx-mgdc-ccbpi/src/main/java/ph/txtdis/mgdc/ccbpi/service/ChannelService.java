package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

public interface ChannelService //
	extends ListedAndResettableService<Channel>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	UniqueNamedService<Channel> {

	List<String> listNames();

	Channel save(String name, LocalDate start) throws Information, Exception;
}