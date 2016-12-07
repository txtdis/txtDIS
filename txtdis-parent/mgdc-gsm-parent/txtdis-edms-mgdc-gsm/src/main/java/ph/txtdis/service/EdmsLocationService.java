package ph.txtdis.service;

import ph.txtdis.dto.Location;

public interface EdmsLocationService extends LocationService {

	Location getBarangay(String barangay, String city);

	Location toBarangay(String barangay);

	Location toCity(String city);

	Location toProvince(String province);
}