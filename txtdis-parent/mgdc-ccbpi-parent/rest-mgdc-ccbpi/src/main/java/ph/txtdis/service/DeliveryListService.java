package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;

public interface DeliveryListService extends QtyPerItemService, SpunBillableService {

	Billable find(Long shipment, String route);

	List<BomEntity> getBomList(LocalDate start, LocalDate end);

	List<BomEntity> getRouteGroupedBomList(LocalDate start, LocalDate end);
}
