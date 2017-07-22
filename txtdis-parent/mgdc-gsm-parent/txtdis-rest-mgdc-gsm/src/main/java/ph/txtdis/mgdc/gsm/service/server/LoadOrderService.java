package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;

public interface LoadOrderService //
		extends BookingService {

	Billable findAsReference(Long id);

	List<Billable> findBooked(LocalDate d);

	Billable find(LocalDate d, String exTruck);

	Billable findWithLoadVariance(LocalDate d);

	Billable findShort(Long id);

	Billable findUnpicked(LocalDate d);
}
