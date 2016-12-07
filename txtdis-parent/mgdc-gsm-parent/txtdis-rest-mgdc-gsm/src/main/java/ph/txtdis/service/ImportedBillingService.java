package ph.txtdis.service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.repository.BillingRepository;

public interface ImportedBillingService
		extends BillableService, DecisionDataUpdate<BillableEntity, BillingRepository>, Imported {
}
