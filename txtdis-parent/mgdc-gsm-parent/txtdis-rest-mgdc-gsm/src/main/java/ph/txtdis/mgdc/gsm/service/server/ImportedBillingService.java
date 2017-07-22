package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;
import ph.txtdis.service.DecisionDataUpdate;

public interface ImportedBillingService //
		extends DecisionDataUpdate<BillableEntity, BillableRepository>, Imported, SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {
}
