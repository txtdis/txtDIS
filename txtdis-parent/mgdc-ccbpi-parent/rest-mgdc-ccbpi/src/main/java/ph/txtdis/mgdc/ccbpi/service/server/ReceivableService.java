package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.ZonedDateTime;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillableRepository;

public interface ReceivableService {

	default BillableEntity setReceivingData(BillableEntity e, Billable b, BillableRepository r) {
		if (b.getReceivedBy() == null || e.getReceivedBy() != null)
			return e;
		e.setReceivedBy(b.getReceivedBy());
		e.setReceivedOn(nowIfReceivedOnNull(b));
		e.setReceivingId(receivingId(b, r));
		return e;
	}

	default ZonedDateTime nowIfReceivedOnNull(Billable b) {
		ZonedDateTime t = b.getReceivedOn();
		return t != null ? t : ZonedDateTime.now();
	}

	default Long receivingId(Billable b, BillableRepository r) {
		Long id = b.getReceivingId();
		return id != null ? id : incrementReceivingId(r);
	}

	default Long incrementReceivingId(BillableRepository r) {
		BillableEntity b = r.findFirstByReceivingIdNotNullOrderByReceivingIdDesc();
		return b == null || b.getReceivingId() == null ? 1L : b.getReceivingId() + 1;
	}
}
