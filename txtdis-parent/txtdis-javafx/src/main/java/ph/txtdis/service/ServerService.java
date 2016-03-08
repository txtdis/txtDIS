package ph.txtdis.service;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Service("serverService")
public class ServerService {

	@Autowired
	Environment env;

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

	public String address() {
		return addresses.get(index());
	}

	public boolean isOffSite() {
		if (asList(env.getActiveProfiles()).contains("dev"))
			return false;
		return location().equals("this.pc");
	}

	public String keystore() {
		System.out.println("Keystore = " + keystores.get(index()) + ".p12");
		System.out.println("Address  = " + address());
		System.out.println("Port  = " + port);
		return keystores.get(index()) + ".p12";
	}

	public String location() {
		if (location == null)
			location = server;
		return location;
	}

	public void location(String location) {
		this.location = location;
	}

	private int index() {
		return locations.indexOf(location());
	}
}
