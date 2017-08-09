package ph.txtdis.mgdc.gsm.service.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;

@Getter
@Setter
@Service("inventoryRestServerService")
public class InventoryRestServerServiceImpl
	implements InventoryRestServerService {

	@Value("${inventory.server.address}")
	private String address;

	@Value("${inventory.server.keystore}")
	private String keystore;

	@Value("${inventory.server.location}")
	private String location;

	@Value("${inventory.server.port}")
	private String port;

	@Override
	public List<String> getLocations() {
		return singletonList(getLocation());
	}
}
