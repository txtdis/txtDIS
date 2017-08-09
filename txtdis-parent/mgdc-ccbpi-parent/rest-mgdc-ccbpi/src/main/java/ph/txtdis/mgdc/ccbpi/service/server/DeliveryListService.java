package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;

public interface DeliveryListService //
	extends QtyPerItemService,
	SpunSavedBillableService {

	Billable find(LocalDate localDate, String route);

	List<BomEntity> getBomList(String route, LocalDate start, LocalDate end);

	List<BillableDetailEntity> getDetailEntityList(String itemVendorNo, String route, LocalDate start, LocalDate end);
}
