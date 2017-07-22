package ph.txtdis.mgdc.gsm.service.server;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("inventoryRestServerService")
public class InventoryRestServerServiceImpl implements InventoryRestServerService {

	@Value("${inventory.server.address}")
	private String address;

	@Value("${inventory.server.keystore}")
	private String keystore;

	@Value("${inventory.server.location}")
	private String location;

	@Value("${inventory.server.port}")
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
