package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

public interface SpunBillableService extends SpunService<Billable, Long>, StartEndDate {

	Billable toBookingIdOnlyBillable(BillableEntity e);

	Billable toOrderNoOnlyBillable(BillableEntity e);

	Billable toSpunIdOnlyBillable(Billable b);

	Billable toTotalValueOnlyBillable(BigDecimal v);

	Billable toDTO(BillableEntity b);

	BillableEntity toEntity(Billable i);
}