package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.TotaledService;

public interface ShippedBillableService //
	extends BillableService,
	TotaledService<BillableDetail> {

	String getRoute();

	default String getShipmentPrompt() {
		return "Shipment No.";
	}
}
