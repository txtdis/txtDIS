package ph.txtdis.service;

import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.util.List;

public interface EdmsLocationService {

	Location getBarangay(String barangay, String city);

	String getBarangay(Customer c);

	String getCity(Customer c);

	String getProvince(Customer c);

	List<Location> listBarangays(String city);

	List<Location> listCities(String province);

	List<Location> listProvinces();

	Location toBarangay(String barangay);

	Location toCity(String city);

	Location toProvince(String province);
}