package ph.txtdis.service;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("serverService")
public class ServerServiceImpl implements ServerService {

	@Value("${server.ip.address}")
	private String ip;

	@Override
	public boolean isMaster() {
		try {
			return InetAddress.getLocalHost().getHostAddress().equals(ip);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isSlave() {
		return !isMaster();
	}
}
