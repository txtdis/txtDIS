package ph.txtdis.service;

import ph.txtdis.exception.NotAllowedOffSiteTransactionException;

public interface BadOrderedBillableService {

	void saveDisposalData() throws NotAllowedOffSiteTransactionException;
}
