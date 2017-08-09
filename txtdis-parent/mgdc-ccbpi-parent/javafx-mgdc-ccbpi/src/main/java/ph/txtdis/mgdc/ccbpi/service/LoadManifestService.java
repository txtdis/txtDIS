package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;

public interface LoadManifestService //
	extends ShippedBillableService {

	void setShipmentDateAndId(LocalDate d, Long id);
}
