package ph.txtdis.service;

import java.util.ArrayList;
import java.util.List;

import ph.txtdis.dto.Channel;

public interface ChannelLimited {

	default Channel getChannelForAll() {
		return new Channel("ALL");
	}

	ChannelService getChannelService();

	default List<Channel> listAllChannels() throws Exception {
		List<Channel> l = new ArrayList<>();
		l.add(getChannelForAll());
		l.addAll(getChannelService().list());
		return l;
	}

	default Channel nullIfAll(Channel f) {
		if (f == null)
			return null;
		return f.equals(getChannelForAll()) ? null : f;
	}
}
