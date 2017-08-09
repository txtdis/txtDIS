package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.service.SellerFilteredService;

public interface BookingVarianceService //
	extends SellerFilteredService<SalesItemVariance>,
	SellerTrackedVarianceService {

	String getRoute();

	void setRoute(String name);
}