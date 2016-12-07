package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Truck;

public interface TruckService extends Listed<Truck>, SavedByName<Truck>, Titled, UniquelyNamed<Truck> {

	boolean isOffSite();

	List<String> listNames();
}