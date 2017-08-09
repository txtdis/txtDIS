package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;

import static java.lang.Math.abs;
import static ph.txtdis.type.PartnerType.EX_TRUCK;

public interface QtyToLoadOrderDetailItemMappingService //
	extends QtyToBillableDetailsItemMappingService {

	default void updateLoadOrderDetailQty(BillableRepository r, Billable b) {
		BillableEntity e = r.findByCustomerTypeAndBookingId(EX_TRUCK, abs(b.getBookingId()));
		if (e != null)
			updateLoadOrderDetailQty(r, e, b);
	}

	default void updateLoadOrderDetailQty(BillableRepository r, BillableEntity e, Billable b) {
		e = updateDetailQty(e, b);
		r.save(e);
	}
}
