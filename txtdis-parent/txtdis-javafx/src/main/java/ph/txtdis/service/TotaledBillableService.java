package ph.txtdis.service;

import ph.txtdis.dto.Billable;

public interface TotaledBillableService {

	Billable updateFinalTotals(Billable b);

	Billable updateInitialTotals(Billable b);
}