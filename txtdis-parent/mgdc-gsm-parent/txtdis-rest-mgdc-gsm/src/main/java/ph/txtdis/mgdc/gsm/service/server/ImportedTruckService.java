package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Truck;
import ph.txtdis.service.ServerTruckService;

import java.util.List;

public interface ImportedTruckService //
	extends ServerTruckService,
	Imported {

	List<Truck> list();

	Truck saveToEdms(Truck t) throws Exception;
}
