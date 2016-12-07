package ph.txtdis.service;

import java.util.List;

public interface RestServerService {

	String address();

	String getPort();

	String getKeystore();

	String getLocation();

	boolean isOffSite();

	List<String> getLocations();

	void setLocation(String text);
}
