package ph.txtdis.service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsTruck;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Truck;

public interface EdmsTruckService //
	extends SavedNameListService<Truck> {

	EdmsTruck findEntityByPlateNo(String plate);

	String getCode(Keyed<Long> r);

	String getDescription();

	Long getId(EdmsInvoice i);
}
