package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

import java.math.BigDecimal;

public interface SpunSavedBillableService //
	extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	Billable toBookingIdOnlyBillable(BillableEntity e);

	BillableEntity toEntity(Billable i);

	Billable toModel(BillableEntity b);

	Billable toOrderNoOnlyBillable(BillableEntity e);

	Billable toSpunIdOnlyBillable(Billable b);

	Billable toTotalValueOnlyBillable(BigDecimal v);
}