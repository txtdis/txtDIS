package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

import java.time.LocalDate;
import java.util.List;

public interface SalesOrderService //
	extends BookingService {

	List<BillableEntity> findAllPicked();

	List<BillableEntity> findAllPickedWithReturns();

	Billable findUnbilledPickedBooking(LocalDate d);

	Billable findUnpicked(LocalDate d);
}
