package ph.txtdis.service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;

public interface EdmsTruckService extends TruckService {

	String getCode(Billable i);

	String getCode(PickList p);

	String getPlateNo(Billable i);

	Long getId(EdmsInvoice i);
}
