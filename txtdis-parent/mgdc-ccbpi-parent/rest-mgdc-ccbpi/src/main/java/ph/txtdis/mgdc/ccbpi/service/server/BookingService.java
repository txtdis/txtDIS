package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface BookingService //
	extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	BillableEntity findEntityByBookingNo(String key);

	List<Billable> findAllUnpicked(LocalDate d);
}
