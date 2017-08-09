package ph.txtdis.service;

import java.util.List;

public interface RestServerService {

	String getAddress();

	String getKeystore();

	String getLocation();

	void setLocation(String text);

	List<String> getLocations();

	String getPort();
}
