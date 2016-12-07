package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Channel;
import ph.txtdis.info.Information;
import ph.txtdis.type.BillingType;

public interface ChannelService extends Listed<Channel>, Titled, UniquelyNamed<Channel> {

	Channel getChannelForAll();

	boolean isOffSite();

	List<Channel> listAllChannels() throws Exception;

	List<String> listNames();

	List<Channel> listVisitedChannels();

	Channel nullIfAll(Channel f);

	Channel save(String name, BillingType type, boolean visited) throws Information, Exception;
}