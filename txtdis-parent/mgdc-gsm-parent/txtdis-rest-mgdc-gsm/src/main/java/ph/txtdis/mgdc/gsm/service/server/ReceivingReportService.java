package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface ReceivingReportService //
		extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	Billable findByBookingId(Long id) throws NotFoundException;
}
