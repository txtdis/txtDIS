package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

public interface SalesOrderService //
		extends BookingService {

	List<BillableEntity> findAllPicked();

	List<BillableEntity> findAllPickedWithReturns();

	Billable findUnbilledPickedBooking(LocalDate d);

	Billable findUnpicked(LocalDate d);
}
