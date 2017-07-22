package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.Truck;
import ph.txtdis.service.ServerTruckService;

public interface ImportedTruckService //
		extends ServerTruckService, Imported {

	List<Truck> list();

	Truck saveToEdms(Truck t) throws Exception;
}
