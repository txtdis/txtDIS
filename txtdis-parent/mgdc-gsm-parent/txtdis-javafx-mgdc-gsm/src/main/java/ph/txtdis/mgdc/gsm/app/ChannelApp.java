package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.fx.table.ChannelTable;
import ph.txtdis.mgdc.gsm.service.ChannelService;

@Scope("prototype")
@Component("channelApp")
public class ChannelApp
	extends AbstractTableApp<ChannelTable, ChannelService, Channel> {
}
