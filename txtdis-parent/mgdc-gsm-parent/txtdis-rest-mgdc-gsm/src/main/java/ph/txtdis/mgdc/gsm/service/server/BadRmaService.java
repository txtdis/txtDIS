package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;

public interface BadRmaService //
	extends RmaService {

	Billable findClosedRmaByCustomerToDetermineAllowanceBalance(Long customerId);
}
