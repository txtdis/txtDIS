package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

public interface DeliveryReportService //
		extends ExTruckBillingService, BillingService {

	BillableEntity toEntity(Billable b);

	Billable toModel(BillableEntity e);
}
