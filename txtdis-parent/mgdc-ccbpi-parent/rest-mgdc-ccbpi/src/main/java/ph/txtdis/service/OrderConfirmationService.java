package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;

public interface OrderConfirmationService extends QtyPerItemService, SpunBillableService {

	Billable find(Long customerVendorId, LocalDate orderDate, Long orderNo);

	Billable findByBookingId(Long id);

	List<Billable> findUnpickedOn(LocalDate localDate);

	List<BomEntity> getRouteGroupedBomList(LocalDate start, LocalDate end);
}
