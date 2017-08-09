package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;

public interface OrderConfirmationService
	extends BookingService,
	FilteredListService,
	QtyPerItemService,
	SpunSavedBillableService {

	Billable find(LocalDate d, Long customerVendorId, Long count);

	List<BomEntity> getBomList(String route, LocalDate start, LocalDate end);

	List<BomEntity> getDeliveredOrderNoAndCustomerAndRouteGroupedBomList();

	List<BillableDetailEntity> getDetailEntityList(String itemVendorNo, String route, LocalDate start, LocalDate end);

	Billable getWithDeliveredValue(String collector, LocalDate start, LocalDate end);

	@Override
	List<BillableEntity> list(String route, LocalDate start, LocalDate end);

	List<BillableEntity> listDelivered(String route, LocalDate start, LocalDate end);

	List<BillableEntity> listUnpicked(String route, LocalDate start, LocalDate end);
}
