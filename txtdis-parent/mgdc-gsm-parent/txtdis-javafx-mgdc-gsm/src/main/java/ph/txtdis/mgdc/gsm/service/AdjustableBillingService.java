package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface AdjustableBillingService {

	Billable setAdjustmentData(Long id, BigDecimal value);

	Customer verifyIsAnEmployee(Long id) throws Exception;
}
