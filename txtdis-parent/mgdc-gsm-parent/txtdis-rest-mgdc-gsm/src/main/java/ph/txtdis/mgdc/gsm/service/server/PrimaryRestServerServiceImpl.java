package ph.txtdis.mgdc.gsm.service.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;

@Getter
@Setter
@Service("primaryRestServerService")
public class PrimaryRestServerServiceImpl
	implements PrimaryRestServerService {

	@Value("${interface.server.address}")
	private String address;

	@Value("${interface.server.keystore}")
	private String keystore;

	@Value("${interface.server.location}")
	private String location;

	@Value("${interface.server.port}")
	private String port;

	@Override
	public List<String> getLocations() {
		return singletonList(getLocation());
	}
}
