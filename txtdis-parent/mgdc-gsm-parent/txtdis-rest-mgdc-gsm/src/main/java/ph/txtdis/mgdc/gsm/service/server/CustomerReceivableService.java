package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.CustomerReceivableReport;

public interface CustomerReceivableService {

	CustomerReceivableReport generateCustomerReceivableReport(Long customerId, long minDaysOver, long maxDaysOver);
}