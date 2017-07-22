package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Service("restServerService")
public class RestServerServiceImpl //
		implements RestServerService {

	private static Logger logger = getLogger(RestServerServiceImpl.class);

	@Autowired
	private Environment env;

	@Value("#{'${server.addresses}'.split(',')}")
	private List<String> addresses;

	@Value("#{'${server.keystores}'.split(',')}")
	private List<String> keystores;

	@Value("#{'${server.locations}'.split(',')}")
	private List<String> locations;

	@Value("${server.default}")
	private String server;

	@Value("${server.port}")
	private String port;

	private String location;

	@Override
	public String address() {
		return addresses.get(index());
	}

	@Override
	public String getKeystore() {
		logger.info("\n    Keystore = " + keystores.get(index()) + ".p12");
		logger.info("\n    Address = " + address());
		logger.info("\n    Port = " + port);
		return keystores.get(index()) + ".p12";
	}

	@Override
	public String getLocation() {
		if (location == null)
			location = server;
		return location;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	private int index() {
		return locations.indexOf(getLocation());
	}
}
