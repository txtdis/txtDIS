package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;
import ph.txtdis.service.DecisionDataUpdate;

public interface BillableService //
		extends DecisionDataUpdate<BillableEntity, BillableRepository>, QtyPerItemService {
}