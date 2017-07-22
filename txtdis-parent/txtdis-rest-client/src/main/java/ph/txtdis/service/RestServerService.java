package ph.txtdis.service;

import java.util.List;

public interface RestServerService {

	String address();

	String getKeystore();

	String getLocation();

	List<String> getLocations();

	String getPort();

	void setLocation(String text);
}
