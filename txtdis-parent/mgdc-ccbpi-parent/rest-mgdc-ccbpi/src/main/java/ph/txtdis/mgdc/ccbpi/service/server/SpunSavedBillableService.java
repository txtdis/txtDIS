package ph.txtdis.mgdc.ccbpi.service.server;

import java.math.BigDecimal;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface SpunSavedBillableService //
		extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	Billable toBookingIdOnlyBillable(BillableEntity e);

	BillableEntity toEntity(Billable i);

	Billable toModel(BillableEntity b);

	Billable toOrderNoOnlyBillable(BillableEntity e);

	Billable toSpunIdOnlyBillable(Billable b);

	Billable toTotalValueOnlyBillable(BigDecimal v);
}