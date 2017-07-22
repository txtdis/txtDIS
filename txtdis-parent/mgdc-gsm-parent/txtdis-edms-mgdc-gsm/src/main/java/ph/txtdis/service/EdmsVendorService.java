package ph.txtdis.service;

import ph.txtdis.domain.EdmsSupervisor;

public interface EdmsVendorService {

	String getAreaCode();

	String getDistrictCode();

	EdmsSupervisor getEdmsSupervisor();

	String getSupervisor();

	String getTerritoryCode();

	void save(EdmsSupervisor s);
}
