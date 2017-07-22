package ph.txtdis.mgdc.gsm.service.server;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("restServerService")
public class RestServerServiceImpl implements MainRestServerService {

	@Value("${interface.server.address}")
	private String address;

	@Value("${interface.server.keystore}")
	private String keystore;

	@Value("${interface.server.location}")
	private String location;

	@Value("${interface.server.port}")
	private String port;

	@Override
	public String address() {
		return address;
	}

	@Override
	public String getKeystore() {
		return keystore;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String getPort() {
		return port;
	}

	@Override
	public List<String> getLocations() {
		return Arrays.asList(getLocation());
	}
}
